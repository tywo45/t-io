package org.tio.examples.im.common.http;

/**
 * 本类直接摘自baseio: https://git.oschina.net/generallycloud/baseio<br>
 * 谢谢作者整理<br>
 * 
 * @author tanyaowu 
 *
 */
public enum HttpResponseStatus {

	/**
	 *初始的请求已经接受，客户应当继续发送请求的其余部分。（HTTP 1.1新）
	 */
	C100(100,"Continue","100 Continue"),
	/**
	 *服务器将遵从客户的请求转换到另外一种协议（HTTP 1.1新）
	 */
	C101(101,"Switching Protocols","101 Switching Protocols"),
	/**
	 *一切正常，对GET和POST请求的应答文档跟在后面。
	 */
	C200(200,"OK","200 OK"),
	/**
	 *服务器已经创建了文档，Location头给出了它的URL。
	 */
	C201(201,"Created","201 Created"),
	/**
	 *已经接受请求，但处理尚未完成。
	 */
	C202(202,"Accepted","202 Accepted"),
	/**
	 *文档已经正常地返回，但一些应答头可能不正确，因为使用的是文档的拷贝（HTTP 1.1新）。
	 */
	C203(203,"Non-Authoritative Information","203 Non-Authoritative Information"),
	/**
	 *没有新文档，浏览器应该继续显示原来的文档。如果用户定期地刷新页面，而Servlet可以确定用户文档足够新，这个状态代码是很有用的。
	 */
	C204(204,"No Content","204 No Content"),
	/**
	 *没有新的内容，但浏览器应该重置它所显示的内容。用来强制浏览器清除表单输入内容（HTTP 1.1新）。
	 */
	C205(205,"Reset Content","205 Reset Content"),
	/**
	 *客户发送了一个带有Range头的GET请求，服务器完成了它（HTTP 1.1新）。
	 */
	C206(206,"Partial Content","206 Partial Content"),
	/**
	 *客户请求的文档可以在多个位置找到，这些位置已经在返回的文档内列出。如果服务器要提出优先选择，则应该在Location应答头指明。
	 */
	C300(300,"Multiple Choices","300 Multiple Choices"),
	/**
	 *客户请求的文档在其他地方，新的URL在Location头中给出，浏览器应该自动地访问新的URL。
	 */
	C301(301,"Moved Permanently","301 Moved Permanently"),
	/**
	 *类似于301，但新的URL应该被视为临时性的替代，而不是永久性的。注意，在HTTP1.0中对应的状态信息是“Moved Temporatily”。出现该状态代码时，浏览器能够自动访问新的URL，因此它是一个很有用的状态代码。注意这个状态代码有时候可以和301替换使用。例如，如果浏览器错误地请求http://host/~user（缺少了后面的斜杠），有的服务器 返回301，有的则返回302。严格地说，我们只能假定只有当原来的请求是GET时浏览器才会自动重定向。请参见307。
	 */
	C302(302,"Found","302 Found"),
	/**
	 *类似于301/302，不同之处在于，如果原来的请求是POST，Location头指定的重定向目标文档应该通过GET提取（HTTP 1.1新）。
	 */
	C303(303,"See Other","303 See Other"),
	/**
	 *客户端有缓冲的文档并发出了一个条件性的请求（一般是提供If-Modified-Since头表示客户只想比指定日期更新的文档）。服务器告 诉客户，原来缓冲的文档还可以继续使用。
	 */
	C304(304,"Not Modified","304 Not Modified"),
	/**
	 *客户请求的文档应该通过Location头所指明的代理服务器提取（HTTP 1.1新）。
	 */
	C305(305,"Use Proxy","305 Use Proxy"),
	/**
	 *和302 （Found）相同。许多浏览器会错误地响应302应答进行重定向，即使原来的请求是POST，即使它实际上只能在POST请求的应答是303时才能重定 向。由于这个原因，HTTP 1.1新增了307，以便更加清除地区分几个状态代码：当出现303应答时，浏览器可以跟随重定向的GET和POST请求；如果是307应答，则浏览器只 能跟随对GET请求的重定向。（HTTP 1.1新）
	 */
	C307(307,"Temporary Redirect","307 Temporary Redirect"),
	/**
	 *请求出现语法错误。
	 */
	C400(400,"Bad Request","400 Bad Request"),
	/**
	 *客户试图未经授权访问受密码保护的页面。应答中会包含一个WWW-Authenticate头，浏览器据此显示用户名字/密码对话框，然后在填 写合适的Authorization头后再次发出请求。
	 */
	C401(401,"Unauthorized","401 Unauthorized"),
	/**
	 *资源不可用。服务器理解客户的请求，但拒绝处理它。通常由于服务器上文件或目录的权限设置导致。
	 */
	C403(403,"Forbidden","403 Forbidden"),
	/**
	 *无法找到指定位置的资源。这也是一个常用的应答。
	 */
	C404(404,"Not Found","404 Not Found"),
	/**
	 *请求方法（GET、POST、HEAD、DELETE、PUT、TRACE等）对指定的资源不适用。（HTTP 1.1新）
	 */
	C405(405,"Method Not Allowed","405 Method Not Allowed"),
	/**
	 *指定的资源已经找到，但它的MIME类型和客户在Accpet头中所指定的不兼容（HTTP 1.1新）。
	 */
	C406(406,"Not Acceptable","406 Not Acceptable"),
	/**
	 *类似于401，表示客户必须先经过代理服务器的授权。（HTTP 1.1新）
	 */
	C407(407,"Proxy Authentication Required","407 Proxy Authentication Required"),
	/**
	 *在服务器许可的等待时间内，客户一直没有发出任何请求。客户可以在以后重复同一请求。（HTTP 1.1新）
	 */
	C408(408,"Request Timeout","408 Request Timeout"),
	/**
	 *通常和PUT请求有关。由于请求和资源的当前状态相冲突，因此请求不能成功。（HTTP 1.1新）
	 */
	C409(409,"Conflict","409 Conflict"),
	/**
	 *所请求的文档已经不再可用，而且服务器不知道应该重定向到哪一个地址。它和404的不同在于，返回407表示文档永久地离开了指定的位置，而 404表示由于未知的原因文档不可用。（HTTP 1.1新）
	 */
	C410(410,"Gone","410 Gone"),
	/**
	 *服务器不能处理请求，除非客户发送一个Content-Length头。（HTTP 1.1新）
	 */
	C411(411,"Length Required","411 Length Required"),
	/**
	 *请求头中指定的一些前提条件失败（HTTP 1.1新）。
	 */
	C412(412,"Precondition Failed","412 Precondition Failed"),
	/**
	 *目标文档的大小超过服务器当前愿意处理的大小。如果服务器认为自己能够稍后再处理该请求，则应该提供一个Retry-After头（HTTP 1.1新）。
	 */
	C413(413,"Request Entity Too Large","413 Request Entity Too Large"),
	/**
	 *URI太长（HTTP 1.1新）。
	 */
	C414(414,"Request URI Too Long","414 Request URI Too Long"),
	/**
	 *服务器不能满足客户在请求中指定的Range头。（HTTP 1.1新）
	 */
	C416(416,"Requested Range Not Satisfiable","416 Requested Range Not Satisfiable"),
	/**
	 *服务器遇到了意料不到的情况，不能完成客户的请求。
	 */
	C500(500,"Internal Server Error","500 Internal Server Error"),
	/**
	 *服务器不支持实现请求所需要的功能。例如，客户发出了一个服务器不支持的PUT请求。
	 */
	C501(501,"Not Implemented","501 Not Implemented"),
	/**
	 *服务器作为网关或者代理时，为了完成请求访问下一个服务器，但该服务器返回了非法的应答。
	 */
	C502(502,"Bad Gateway","502 Bad Gateway"),
	/**
	 *服务器由于维护或者负载过重未能应答。例如，Servlet可能在数据库连接池已满的情况下返回503。服务器返回503时可以提供一个 Retry-After头。
	 */
	C503(503,"Service Unavailable","503 Service Unavailable"),
	/**
	 *由作为代理或网关的服务器使用，表示不能及时地从远程服务器获得应答。（HTTP 1.1新）
	 */
	C504(504,"Gateway Timeout","504 Gateway Timeout"),
	/**
	 *服务器不支持请求中所指明的HTTP版本。（HTTP 1.1新）
	 */
	C505(505,"HTTP Version Not Supported","505 HTTP Version Not Supported");

	
	private int status;
	
	private String description;
	
	private String headerText;
	
	private byte [] headerBinary;

	private HttpResponseStatus(int status, String description, String headerText) {
		this.status = status;
		this.description = description;
		this.headerText = headerText;
		this.headerBinary = headerText.getBytes();
	}

	public int getStatus() {
		return status;
	}

	public String getDescription() {
		return description;
	}

	public String getHeaderText() {
		return headerText;
	}
	
	public byte[] getHeaderBinary() {
		return headerBinary;
	}
	
	public static HttpResponseStatus getHttpStatus(int status){
		HttpResponseStatus [] values = HttpResponseStatus.values();
		
		for(HttpResponseStatus v : values){
			
			if (v.getStatus() == status) {
				return v;
			}
		}
		
		return C505;
	}
	
}
