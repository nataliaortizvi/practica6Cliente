package com.example.practica6cliente;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.example.practica6cliente.model.Usuario;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    Button btIngresar;
    EditText txUser, txContra;
    String us, con;


    //variables tcp
    Socket socket;
    BufferedWriter writer;
    BufferedReader reader;
    String line;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btIngresar = findViewById(R.id.btIngresar);
        txUser = findViewById(R.id.txUser);
        txContra = findViewById(R.id.txContra);

        iniciarCliente();


        //cuando hace clic en ingresar manda los mensajes por el metodo sendMessage
        btIngresar.setOnClickListener(
                (v)->{

                    //se serializan los datos de la clase usuario que se van a mandar
                    Gson gson = new Gson();
                    us = txUser.getText().toString().trim();
                    con = txContra.getText().toString().trim();

                    Usuario user = new Usuario(us,con);
                    String json = gson.toJson(user);

                    //se manda el gson
                    sendMessage(json);

                    Log.e(">>>>", ""+json);
                }
        );
    }


    public void iniciarCliente(){
        new Thread(
                () -> {
                    try {
                        ////// IP CON EL COMPUTADOR (sin emulador)
                        //socket = new Socket("192.168.2.101", 5000);

                        ////// IP CON EL EMULADOR
                        socket = new Socket("10.0.2.2", 5000);

                        //emisor
                        OutputStream os = socket.getOutputStream();
                        writer = new BufferedWriter (new OutputStreamWriter(os));

                        //receptor
                        InputStream is = socket.getInputStream();
                        reader = new BufferedReader (new InputStreamReader(is));

                        while(true) {
                            //lectura de lo que manda el server
                            line = reader.readLine();
                            Log.e("lleganding",""+line);

                            runOnUiThread(
                                    () -> {
                                        //si el server manda mensaje bueno entonces cambio a la pantalla de bienvenido
                                        if(line.contains("good")){
                                            Intent i = new Intent(this, pantallaBienvenido.class);
                                            startActivity(i);

                                        }
                                        //si el server manda mensaje malo entonces hago un toast de datos incorrectos
                                        if(line.contains("bad")){
                                            Toast.makeText(this, "El usuario y contraseña no son correctos", Toast.LENGTH_LONG).show();
                                        }
                                    }
                            );
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        ).start();
    }

    public void sendMessage(String mensaje) {

        new Thread(
                () -> {
                    try {
                        writer.write(mensaje + "\n");
                        writer.flush();

                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }

        ).start();
    }
}