package adrien.tisonad.channelmessaging;

import android.os.AsyncTask;

import java.util.HashMap;

/**
 * Created by tisonad on 20/01/2017.
 */
public class Downloader extends AsyncTask<HashMap<String, String>,Boolean,String> {

    @Override
    protected String doInBackground(HashMap<String, String>... params) {
        String connexion = "";
        LoginActivity log = new LoginActivity();
        for(HashMap<String, String> param : params)
            connexion = log.performPostCall("http://www.raphaelbischof.fr/messaging/?function=connect",param);
        return connexion;
    }
}
