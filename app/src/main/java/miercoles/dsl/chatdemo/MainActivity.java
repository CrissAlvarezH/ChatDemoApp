package miercoles.dsl.chatdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.EmptyStackException;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_NOMBRE = "nombre";
    public static final String EXTRA_USUARIOS  = "usuarios";
    private final String TAG = "ActividadSocket";

    private TextView txtTexto;
    private EditText edtTexto;
    private Button btnEnviarTexto;

    private Socket socket;
    private OnNuevoTexto onNuevoTexto;
    private String nombre;
    private Spinner spnConectados;

    {
        try {
            socket = IO.socket("http://167.99.52.15:3000");
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
        spnConectados = findViewById(R.id.spn_conectados);

        Bundle extras = getIntent().getExtras();
        if(extras != null && extras.getString(EXTRA_NOMBRE) != null){
            nombre = extras.getString(EXTRA_NOMBRE);
            String[] usuarios = extras.getStringArray(EXTRA_USUARIOS);

            Log.v(TAG, "Longitud de usuarios "+usuarios.length);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, usuarios);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnConectados.setAdapter( adapter );

            if(getSupportActionBar() != null){
                getSupportActionBar().setTitle(nombre);
            }
        }else{
            Toast.makeText(this, "Debe loguearse", Toast.LENGTH_SHORT).show();
            finish();
        }


        onNuevoTexto = new OnNuevoTexto();

        socket.on("nuevoTexto", onNuevoTexto);// agregamos un listener
        socket.on("listarUsuarios", onListarUsuarios);

        socket.connect();// Nos conectamos al socket
    }

    public void clickEnviar(View btn){
        JSONObject jsonEnviar = new JSONObject();
        try {
            jsonEnviar.put("para", spnConectados.getSelectedItem().toString());
            jsonEnviar.put("texto", edtTexto.getText().toString());

            socket.emit("nuevoTexto", jsonEnviar.toString());
        } catch (JSONException e) {
            Toast.makeText(this, "Error al enviar", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

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

    private Emitter.Listener onListarUsuarios = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.v(TAG, "Listar argumentos"+ args[0]);
            final JSONArray usuariosJson = (JSONArray) args[0];

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String [] usuarios = new String[usuariosJson.length()];

                    for(int i=0; i<usuariosJson.length(); i++){
                        try {
                            usuarios[i] = usuariosJson.getString(i);
                        } catch (JSONException e) {
                            Toast.makeText(MainActivity.this, "Error casteando los usuarios", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, usuarios);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnConectados.setAdapter( adapter );
                }
            });

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        socket.disconnect();
        if(onNuevoTexto != null)
            socket.off("nuevoTexto", onNuevoTexto);// quitamos el listener

        if(onListarUsuarios != null)
            socket.off("listarUsuarios", onListarUsuarios);
    }
}
