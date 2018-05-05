package miercoles.dsl.chatdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "ActividadSocket";

    private TextView txtTexto;
    private EditText edtTexto;
    private Button btnEnviarTexto;

    private Socket socket;
    private OnNuevoTexto onNuevoTexto;

    {
        try {
            socket = IO.socket("http://192.168.100.53:3000");
        } catch (URISyntaxException e) {
            Log.e(TAG, "Erro al crear el socket");
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtTexto = findViewById(R.id.txt_texto);
        edtTexto = findViewById(R.id.edt_texto);
        btnEnviarTexto = findViewById(R.id.btn_enviar);

        onNuevoTexto = new OnNuevoTexto();

        socket.on("nuevoTexto", onNuevoTexto);// agregamos un listener

        socket.connect();// Nos conectamos al socket
    }

    public void clickEnviar(View btn){
        socket.emit("nuevoTexto", edtTexto.getText().toString());
    }

    private class OnNuevoTexto implements Emitter.Listener {

        private String texto;

        @Override
        public void call(final Object... args) {
            Log.v(TAG, "Nuevo evento");

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    try {
                        JSONObject json = (JSONObject) args[0];
                        texto = json.getString("texto");
                    } catch (JSONException e) {
                        texto = "Error al obtener texto";

                        Log.e(TAG, "Error al obtener texto del JSON");
                        e.printStackTrace();
                    }

                    txtTexto.setText(texto);
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        socket.disconnect();
        if(onNuevoTexto != null)
            socket.off("nuevoTexto", onNuevoTexto);// quitamos el listener
    }
}
