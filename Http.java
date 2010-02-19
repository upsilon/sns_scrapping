import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.net.*;
import java.util.*;

/**
 * HTTPによる通信をサポートする機能を提供します。
 * 
 * GET･POSTメソッドによる送受信を行い、結果を取得するクラスです。<br>
 * Cookieは保持される(保持してくれる)ので、Cookie必須のWebページでも通信が可能です。
 */
public class Http
{
  /**
   * 通信に使用する文字コード
   */
  private String enc;

  /**
   * Cookie管理マネージャ
   */
  private CookieManager cc;

  /**
   * 文字コードを自動で判別するHttpクラスのインスタンスを作成します。
   * できれば手動で文字コードを指定してください。
   */
  public Http()
  {
    // 何も指定されなければ、自動で文字コードを判定させる。
    // もちろん、必ずしも正しい判定がされるわけではないので、
    // あらかじめ文字コードを調べてた方が良いのは言うまでもない。
    this("JISAutoDetect");
  }

  /**
   * 指定されたエンコーディングを使用するHttpクラスのインスタンスを作成します。
   * 
   * @param enc 通信で使用するエンコーディング
   */
  public Http(String enc)
  {
    this.enc = enc;
    this.cc = new CookieManager();

    // ここでデフォルトのCookieManagerを設定すると、
    // これ以降のCoockieの管理は自動でやってくれる。
    cc.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
    CookieHandler.setDefault(cc);
  }

  /**
   * GETメソッドによる送受信を行い、結果を返します。
   * 
   * @param url URL
   * @param getData パラメータ
   * @return レスポンス内容
   * @throws IOException 入出力エラーが発生すると返します (そりゃそうだ)
   */
  public String get(String url, Map<String, String> getData)
    throws IOException
  {
    URL u = new URL(url + createGetParam(getData));
    HttpURLConnection c = (HttpURLConnection) u.openConnection();

    c.connect();

    return readURLConnection(c);
  }

  /**
   * GETメソッドによる送受信を行い、結果を返します。
   * 
   * @param url URL
   * @return レスポンス内容
   * @throws IOException 入出力エラーが発生すると返します
   */
  public String get(String url) throws IOException
  {
    return get(url, null);
  }

  /**
   * POSTメソッドによる送受信を行い、結果を返します。
   * 
   * @param url URL
   * @param postData パラメータ
   * @return レスポンス内容
   * @throws IOException 入出力エラーが発生すると返します
   */
  public String post(String url, Map<String, String> postData)
    throws IOException
  {
    URL u = new URL(url);
    String postparam = createParam(postData);
    HttpURLConnection c = (HttpURLConnection) u.openConnection();

    c.setDoOutput(true);//POST可能にする
    c.setRequestMethod("POST");
    c.setRequestProperty("ContentType", "application/x-www-form-urlencoded");
    c.setRequestProperty("ContentLength", Integer.toString(postparam.length()));

    c.connect();

    writeURLConnection(c, postparam.getBytes(enc));

    return readURLConnection(c);
  }

  /**
   * POSTメソッドによる文字列およびファイルの送受信を行い、結果を返します。
   * 
   * @param url URL
   * @param postData パラメータ
   * @param fileData 送信するファイル
   * @return レスポンス内容
   * @throws IOException 入出力エラーが発生すると返します
   */
  public String post(String url, Map<String, String> postData,
    Map<String, File> fileData) throws IOException
  {
    URL u = new URL(url);
    String boundary = Long.toString(System.currentTimeMillis());
    HttpURLConnection c = (HttpURLConnection) u.openConnection();

    c.setDoOutput(true);//POST可能にする
    c.setRequestMethod("POST");
    c.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

    c.connect();

    OutputStream output = c.getOutputStream();
    StringBuilder paramP;
    for (Map.Entry<String, String> p : postData.entrySet())
    {
      String key = URLEncoder.encode(p.getKey(), enc);
      String value = URLEncoder.encode(p.getValue(), enc);

      paramP = new StringBuilder();
      paramP.append("--")
            .append(boundary)
            .append("\r\n")

            .append("Content-Disposition: form-data; name=\"")
            .append(key)
            .append("\"\r\n\r\n")

            .append(value)
            .append("\r\n");

      output.write(paramP.toString().getBytes(enc));
    }

    output.write("\r\n".getBytes(enc));

    for (Map.Entry<String, File> p : fileData.entrySet())
    {
      File file = p.getValue();
      String key = URLEncoder.encode(p.getKey(), enc);
      String fname = URLEncoder.encode(file.getName(), enc);

      // StringBuilder でも + 演算子が使えると良いんだけどなぁ…
      paramP = new StringBuilder();
      paramP.append("--")
            .append(boundary)
            .append("\r\n")
            .append("Content-Disposition: form-data; ")
            .append("name=\"").append(key).append("\"; ")
            .append("filename=\"").append(fname).append("\"\r\n")
            .append("Content-Type: application/octet-stream\r\n")
            .append("Content-Transfer-Encoding: binary\r\n\r\n");
      output.write(paramP.toString().getBytes(enc));

      FileInputStream input = new FileInputStream(file);
      FileChannel inChannel = input.getChannel();
      WritableByteChannel outChannel = Channels.newChannel(output);

      inChannel.transferTo(0, inChannel.size(), outChannel);

      inChannel.close();
    }

    output.write(("--" + boundary + "-").getBytes());

    output.close();

    return readURLConnection(c);
  }

  /**
   * POSTメソッドおよびGETメソッドによる送受信を行い、結果を返します。
   * 
   * @param url URL
   * @param getData GETパラメータ
   * @param postData POSTパラメータ
   * @return レスポンス内容
   * @throws IOException 入出力エラーが発生すると返します
   */
  public String postAndGet(String url, Map<String, String> getData,
    Map<String, String> postData) throws IOException
  {
    return post(url + createGetParam(getData), postData);
  }

  /**
   * POSTメソッドおよびGETメソッドによる送受信を行い、結果を返します。
   * 
   * @param url URL
   * @param getData GETパラメータ
   * @param postData POSTパラメータ
   * @param fileData 送信するファイル
   * @return レスポンス内容
   * @throws IOException 入出力エラーが発生すると返します
   */
  public String postAndGet(String url, Map<String, String> getData,
    Map<String, String> postData, Map<String, File> fileData)
    throws IOException
  {
    return post(url + createGetParam(getData), postData, fileData);
  }

  /**
   * 保存されているCookieを取得します。
   * 
   * @param url CookieのURL
   * @param name Cookieの名前
   * @return 取得したCookie
   */
  public String getCookie(URL url, String name)
  {
    try
    {
      CookieStore store = cc.getCookieStore();
      for (HttpCookie cookie : store.get(url.toURI()))
      {
        if (cookie.getName().equals(name))
        {
          return cookie.getValue();
        }
      }
    }
    catch (URISyntaxException e)
    {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Cookieをセットします。
   * 
   * @param url URL
   * @param name 名前
   * @param value 値
   */
  public void setCookie(URL url, String name, String value)
  {
    try
    {
      cc.getCookieStore().add(url.toURI(), new HttpCookie(name, value));
    }
    catch (URISyntaxException e)
    {
      e.printStackTrace();
    }
  }

  /**
   * Cookieの個数を返します。
   * 
   * @param url 対象となるURL
   * @return 個数
   */
  public int cookieLength(URL url)
  {
    try
    {
      return cc.getCookieStore().get(url.toURI()).size();
    }
    catch (URISyntaxException e)
    {
      e.printStackTrace();
    }
    return -1;
  }

  /**
   * GETメソッド用のパラメータを生成する。
   * 
   * @param param パラメータの一覧
   * @return GETメソッド用パラメータ
   */
  public String createGetParam(Map<String, String> param)
  {
    if (param == null)
    {
      return "";
    }
    return "?" + createParam(param);
  }

  /**
   * "key=value" 形式のパラメータを生成する。
   * 
   * @param param パラメータ一覧
   * @return 生成したパラメータ
   */
  public String createParam(Map<String, String> param)
  {
    if (param == null)
    {
      return "";
    }

    StringBuilder result = new StringBuilder();
    for(Map.Entry<String, String> p : param.entrySet())
    {
      try
      {
        String key = URLEncoder.encode(p.getKey(), enc);
        String value = URLEncoder.encode(p.getValue(), enc);

        result.append(key).append('=').append(value).append('&');
      }
      catch (UnsupportedEncodingException e)
      {
        e.printStackTrace();
      }
    }
    return result.toString();
  }

  // 以下、privateメソッド

  /**
   * URLConnectionの出力ストリームを開き、データを送る。
   * 
   * @param uc 送信先のURLConnection
   * @param data 送信するデータ
   * @throws IOException 入出力エラーが発生ｓ（ｒｙ
   */
  private void writeURLConnection(HttpURLConnection uc, byte data[])
    throws IOException
  {
    OutputStream output = uc.getOutputStream();
    output.write(data);
    output.close();
  }

  /**
   * URLConnectionの入力ストリームを開き、データを受信。
   * 
   * @param uc 受信先のURLConnection
   * @return 受信したデータ
   * @throws IOException 入出力エラーがはっｓ（ｒｙ
   */
  private String readURLConnection(HttpURLConnection uc)
    throws IOException
  {
    BufferedReader reader = new BufferedReader(
      new InputStreamReader(uc.getInputStream(), enc));

    StringBuilder text = new StringBuilder();
    String line;
    while ((line = reader.readLine()) != null)
    {
      text.append(line);
      text.append('\n');
    }
    reader.close();

    return text.toString();
  }
}
