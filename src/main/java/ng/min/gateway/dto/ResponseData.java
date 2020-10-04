package ng.min.gateway.dto;

/**
 * Created by Odinaka Onah on 04 Oct, 2020.
 */
public class ResponseData{
	private String token;
//	private String tokenOld;
	private String expiration;

	public ResponseData() {
	}

	public ResponseData(String token) {
		this.token = token;
//		this.tokenOld = tokenOld;
//		this.expiration = expiration;
	}

	public String getToken() {
		return token;
	}

	public ResponseData setToken(String token) {
		this.token = token;
		return this;
	}

//	public String getTokenOld() {
//		return tokenOld;
//	}
//
//	public void setTokenOld(String tokenOld) {
//		this.tokenOld = tokenOld;
//	}

	public String getExpiration() {
		return expiration;
	}

	public void setExpiration(String expiration) {
		this.expiration = expiration;
	}

	@Override
	public String toString() {
		return "ResponseData{" +
				"token='" + token + '\'' +
//				", tokenOld='" + tokenOld + '\'' +
				", expiration='" + expiration + '\'' +
				'}';
	}
}
