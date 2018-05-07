package miercoles.dsl.chatdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class LoginActivity extends AppCompatActivity {
    private final String TAG = "LoginActividad";

    private EditText edtNombre;

    private Socket socket;


    {
        try {
            socket = IO.socket("http://167.99.52.15:3000");// ip de vendedorweb
        } catch (URISyntaxException e) {
            Log.e(TAG, "Error al crear el socket");
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtNombre = findViewById(R.id.edt_nombre);

        socket.on("usuarioAdd", onUsuarioRegistrado);
        socket.connect();
    }

    public void clickEntrar(View btn){
        if(!edtNombre.getText().toString().trim().isEmpty()) {
            socket.emit("nuevoUsuario", edtNombre.getText().toString());
        }else{
            edtNombre.setError("Escriba su nombre");
            edtNombre.requestFocus();
        }
    }

    private Emitter.Listener onUsuarioRegistrado = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject mensaje = (JSONObject) args[0];

            try {
                if(mensaje != null && mensaje.getString("mensaje").equals("usuario_add")){
                    JSONArray usuariosJson = mensaje.getJSONArray("usuarios");

                    String [] usuarios = new String[usuariosJson.length()];

                    for(int i=0; i<usuariosJson.length(); i++){
                        try {
                            usuarios[i] = usuariosJson.getString(i);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra(MainActivity.EXTRA_NOMBRE, edtNombre.getText().toString());
                    intent.putExtra(MainActivity.EXTRA_USUARIOS, usuarios);
                    startActivity(intent);
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //socket.disconnect();
        socket.off("usuarioAdd", onUsuarioRegistrado);
    }
}
