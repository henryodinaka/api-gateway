package ng.min.gateway.dto;


/**
 * Created by Odinaka Onah on 04 Oct, 2020.
 */

public class GatewayResult {

	private String code;
	private String msg;
	//  private boolean isAccountText;
//	private ResponseData data;

	public GatewayResult() {
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
//
//	public ResponseData getData() {
//		return data;
//	}
//
//	public void setData(ResponseData data) {
//		this.data = data;
//	}

	@Override
	public String toString() {
		return "Response{" +
				"code='" + code + '\'' +
				", msg='" + msg + '\'' +
//				", data=" + data +
				'}';
	}
}
