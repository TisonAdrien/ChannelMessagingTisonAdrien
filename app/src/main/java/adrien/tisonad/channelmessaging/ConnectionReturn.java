package adrien.tisonad.channelmessaging;

/**
 * Created by tisonad on 23/01/2017.
 */
public class ConnectionReturn {
    public String response;
    public String code;
    public String accesstoken;

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAccesstoken() {
        return accesstoken;
    }

    public void setAccesstoken(String accesstoken) {
        this.accesstoken = accesstoken;
    }


    public ConnectionReturn(String response, String code, String accesstoken){
        this.response = response;
        this.code = code;
        this.accesstoken = accesstoken;
    }

    public String toString(){
        return "Reponse{ response='"+response+
                "\', code='"+code+
                "\',accesstoken='" + accesstoken +"\'}";
    }

}
