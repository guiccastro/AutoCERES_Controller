package com.example.ac;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.Calendar;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


public class MainActivity extends AppCompatActivity {

    private Button bico1, bico2, bico3, bico4, bico5, bico6, bico7, bico8, bico9, bico10;
    private Button btTurnOff, btTurnOn;
    TextView valPressao1, valPressao2, valVasao1, valVasao2, textPressao1, textPressao2,
    textVazao1, textVazao2, uniPressao1, uniPressao2, uniVazao1, uniVazao2, textValores;
    private ImageButton bluetooth,  btWriteFile;

    String file_name = "OUTPUT.txt";
    String P1, P2, V1, V2;
    String output_valores = "";
    char txt = '1';
    int cont_input = 0;
    File path = new File(Environment.getExternalStorageDirectory() + File.separator + "AutoCERES");

    Date currenTime;
    DateFormat dateFormat;

    String bt1 = "OFF", bt2 = "OFF", bt3 = "OFF", bt4 = "OFF", bt5 = "OFF",
    bt6 = "OFF", bt7 = "OFF", bt8 = "OFF", bt9 = "OFF", bt10 = "OFF";

    /*Bluetooth*/
    BluetoothAdapter meuBluetoothAdapter = null;
    private static String MAC = null;
    BluetoothDevice meuDevice = null;
    BluetoothSocket meuSocket = null;
    UUID UUID_SERIAL_PORT = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    ConnectedThread connectedThread;

    /*Variaveis de controle*/
    private static final int SOLICITA_ATIVACAO_BT = 1;
    private static final int SOLICITA_CONEXAO_BT = 2;
    private static final int MENSAGEM_RECEBIDA_BT = 3;
    boolean conectado = false;

    android.os.Handler meuHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case MENSAGEM_RECEBIDA_BT:
                    String tempMsg = (String) msg.obj;

                    P1 = tempMsg.substring(1,5);
                    P2 = tempMsg.substring(tempMsg.indexOf('B')+1,tempMsg.indexOf('B') + 5);
                    V1 = tempMsg.substring(tempMsg.indexOf('C')+1,tempMsg.indexOf('C') + 5);
                    V2 = tempMsg.substring(tempMsg.indexOf('D')+1);

                    valPressao1.setText(P1);
                    valPressao2.setText(P2);
                    valVasao1.setText(V1);
                    valVasao2.setText(V2);

                    cont_input = cont_input + 1;

                    if(cont_input == 60){
                        cont_input = 0;
                        currenTime = Calendar.getInstance().getTime();
                        dateFormat = new SimpleDateFormat("dd/mm/yyyy HH:mm:ss");
                        file_name = dateFormat.format(currenTime).substring(11) + ".txt";

                        writeFile(output_valores, file_name);

                        output_valores = "";
                    }

                    /*currenTime = Calendar.getInstance().getTime();
                    dateFormat = new SimpleDateFormat("dd/mm/yyyy HH:mm:ss");

                    output_valores = output_valores + dateFormat.format(currenTime) + " P1: " + P1 + " P2: " + P2 + " V1: " + V1 + " V2: " + V2 + "\n";
                    */

                    gravaValores();

                    break;
            }
            return false;
        }
    });

    private String [] permissoesNecessarias = new String[]{
            Manifest.permission.BLUETOOTH
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Permissao.validaPermissoes(1, this, permissoesNecessarias );

        meuBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        /*Verifica se o aparelho realmente tem bluetooth ou nao*/
        if (meuBluetoothAdapter == null){
            /*Aparelho nao possui bluetooth*/
            Toast.makeText(getApplicationContext(), "O aparelho não possui Bluetooth, o app será encerrado!", Toast.LENGTH_LONG).show();
            finish();
        } else {
                /*Foi encontrado um adaptador bluetooth no aparelho
                Sera verificado se o mesmo esta ligado ou nao */
            if(!meuBluetoothAdapter.isEnabled()) {
                /* Caso nao esteja ligado sera solicitado ao usuario para que ative ele */
                Intent ativaBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(ativaBluetooth, SOLICITA_ATIVACAO_BT);
            }
        }

        bluetooth = findViewById(R.id.btBluetooth);
        bico1 = findViewById(R.id.switch1);
        bico2 = findViewById(R.id.switch2);
        bico3 = findViewById(R.id.switch3);
        bico4 = findViewById(R.id.switch4);
        bico5 = findViewById(R.id.switch5);
        bico6 = findViewById(R.id.switch6);
        bico7 = findViewById(R.id.switch7);
        bico8 = findViewById(R.id.switch8);
        bico9 = findViewById(R.id.switch9);
        bico10 = findViewById(R.id.switch10);
        btTurnOff = findViewById(R.id.btTurnOff);
        btTurnOn = findViewById(R.id.btTurnOn);
        valPressao1 = findViewById(R.id.valPressao1);
        valPressao2 = findViewById(R.id.valPressao2);
        valVasao1 = findViewById(R.id.valVazao1);
        valVasao2 = findViewById(R.id.valVazao2);
        textPressao1 = findViewById(R.id.textPressao1);
        textPressao2 = findViewById(R.id.textPressao2);
        textVazao1 = findViewById(R.id.textVazao1);
        textVazao2 = findViewById(R.id.textVazao2);
        uniPressao1 = findViewById(R.id.uniPressao1);
        uniPressao2 = findViewById(R.id.uniPressao2);
        uniVazao1 = findViewById(R.id.uniVazao1);
        uniVazao2 = findViewById(R.id.uniVazao2);
        textValores = findViewById(R.id.textValores);
        btWriteFile = findViewById(R.id.btWriteFile);

        changeColorAllTrue();

        if(!path.exists()) {
            path.mkdirs();
        }

        btTurnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeColorAllFalse();
                /*
                currenTime = Calendar.getInstance().getTime();
                dateFormat = new SimpleDateFormat("dd/mm/yyyy HH:mm:ss");

                output_valores = output_valores + dateFormat.format(currenTime) + " B1:OFF B2:OFF B3:OFF B4:OFF B5:OFF B6:OFF B7:OFF B8:OFF B9:OFF B10:OFF\n";
                */

                gravaValores();

                txt = 'k';
                String s = String.valueOf(txt);

                bt1 = "OFF";
                bt2 = "OFF";
                bt3 = "OFF";
                bt4 = "OFF";
                bt5 = "OFF";
                bt6 = "OFF";
                bt7 = "OFF";
                bt8 = "OFF";
                bt9 = "OFF";
                bt10 = "OFF";

                /*Verifica se esta conectado */
                if (conectado) {
                    connectedThread.enviar(s);
                    } else {
                        Toast.makeText(getApplicationContext(), "Bluetooth não está conectado!", Toast.LENGTH_SHORT).show();
                    }
                }
        });

        btTurnOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeColorAllTrue();
                /*
                currenTime = Calendar.getInstance().getTime();
                dateFormat = new SimpleDateFormat("dd/mm/yyyy HH:mm:ss");

                output_valores = output_valores + dateFormat.format(currenTime) + " B1:ON B2:ON B3:ON B4:ON B5:ON B6:ON B7:ON B8:ON B9:ON B10:ON\n";
                */

                gravaValores();

                txt = 'K';
                String s = String.valueOf(txt);

                bt1 = "ON";
                bt2 = "ON";
                bt3 = "ON";
                bt4 = "ON";
                bt5 = "ON";
                bt6 = "ON";
                bt7 = "ON";
                bt8 = "ON";
                bt9 = "ON";
                bt10 = "ON";

                /*Verifica se esta conectado */
                if (conectado) {
                    connectedThread.enviar(s);
                } else {
                    Toast.makeText(getApplicationContext(), "Bluetooth não está conectado!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (conectado) {
                    /*Tenta desconectar o dispositivo */
                    try {
                        meuSocket.close();
                        conectado = false;

                        Toast.makeText(getApplicationContext(), "Bluetooth DESCONECTADO", Toast.LENGTH_LONG).show();
                    } catch (IOException erro) {
                        Toast.makeText(getApplicationContext(), "Ocorreu um erro ao desconectar!" + "\n" + "ERRO :" + erro, Toast.LENGTH_LONG).show();

                    }

                } /*Caso nao esteja conectado ainda */ else {
                    /* Abre uma janela com os dispositivos disponiveis */
                    Intent abreListaDispositivos = new Intent(MainActivity.this, ListaDispositivosBluetooth.class);
                    startActivityForResult(abreListaDispositivos, SOLICITA_CONEXAO_BT);
                }
            }
        });

        btWriteFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currenTime = Calendar.getInstance().getTime();
                dateFormat = new SimpleDateFormat("dd/mm/yyyy HH:mm:ss");
                file_name = dateFormat.format(currenTime).substring(11) + ".txt";

                writeFile(output_valores, file_name);

                output_valores = "";
            }
        });

        bico1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (bt1 == "OFF") {
                    bt1 = "ON";
                    changeColorTrue(bico1);
                    Log.d("switch", "Checked 1");
                    txt = 'A';
                } else {
                    bt1 = "OFF";
                    changeColorFalse(bico1);
                    Log.d("switch", "Not Checked 1");
                    txt = 'a';
                }

                gravaValores();

                String s = String.valueOf(txt);

                /*Verifica se esta conectado */
                if (conectado) {
                    connectedThread.enviar(s);
                } else {
                    Toast.makeText(getApplicationContext(), "Bluetooth não está conectado!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        bico2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (bt2 == "OFF") {
                    bt2 = "ON";
                    changeColorTrue(bico2);
                    Log.d("switch", "Checked 2");
                    txt = 'B';
                } else {
                    bt2 = "OFF";
                    changeColorFalse(bico2);
                    Log.d("switch", "Not Checked 2");
                    txt = 'b';
                }

                gravaValores();

                String s = String.valueOf(txt);

                /*Verifica se esta conectado */
                if (conectado) {
                    connectedThread.enviar(s);
                } else {
                    Toast.makeText(getApplicationContext(), "Bluetooth não está conectado!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        bico3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (bt3 == "OFF") {
                    bt3 = "ON";
                    changeColorTrue(bico3);
                    Log.d("switch", "Checked 3");
                    txt = 'C';
                } else {
                    bt3 = "OFF";
                    changeColorFalse(bico3);
                    Log.d("switch", "Not Checked 3");
                    txt = 'c';
                }

                gravaValores();

                String s = String.valueOf(txt);

                /*Verifica se esta conectado */
                if (conectado) {
                    connectedThread.enviar(s);
                } else {
                    Toast.makeText(getApplicationContext(), "Bluetooth não está conectado!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        bico4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (bt4 == "OFF") {
                    bt4 = "ON";
                    changeColorTrue(bico4);
                    Log.d("switch", "Checked 4");
                    txt = 'D';
                } else {
                    bt4 = "OFF";
                    changeColorFalse(bico4);
                    Log.d("switch", "Not Checked 4");
                    txt = 'd';
                }

                gravaValores();

                String s = String.valueOf(txt);

                /*Verifica se esta conectado */
                if (conectado) {
                    connectedThread.enviar(s);
                } else {
                    Toast.makeText(getApplicationContext(), "Bluetooth não está conectado!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        bico5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (bt5 == "OFF") {
                    bt5 = "ON";
                    changeColorTrue(bico5);
                    Log.d("switch", "Checked 5");
                    txt = 'E';
                } else {
                    bt5 = "OFF";
                    changeColorFalse(bico5);
                    Log.d("switch", "Not Checked 5");
                    txt = 'e';
                }

                gravaValores();

                String s = String.valueOf(txt);

                /*Verifica se esta conectado */
                if (conectado) {
                    connectedThread.enviar(s);
                } else {
                    Toast.makeText(getApplicationContext(), "Bluetooth não está conectado!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        bico6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (bt6 == "OFF") {
                    bt6 = "ON";
                    changeColorTrue(bico6);
                    Log.d("switch", "Checked 6");
                    txt = 'F';
                } else {
                    bt6 = "OFF";
                    changeColorFalse(bico6);
                    Log.d("switch", "Not Checked 6");
                    txt = 'f';
                }

                gravaValores();

                String s = String.valueOf(txt);

                /*Verifica se esta conectado */
                if (conectado) {
                    connectedThread.enviar(s);
                } else {
                    Toast.makeText(getApplicationContext(), "Bluetooth não está conectado!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        bico7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (bt7 == "OFF") {
                    bt7 = "ON";
                    changeColorTrue(bico7);
                    Log.d("switch", "Checked 7");
                    txt = 'G';
                } else {
                    bt7 = "OFF";
                    changeColorFalse(bico7);
                    Log.d("switch", "Not Checked 7");
                    txt = 'g';
                }

                gravaValores();

                String s = String.valueOf(txt);

                /*Verifica se esta conectado */
                if (conectado) {
                    connectedThread.enviar(s);
                } else {
                    Toast.makeText(getApplicationContext(), "Bluetooth não está conectado!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        bico8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (bt8 == "OFF") {
                    bt8 = "ON";
                    changeColorTrue(bico8);
                    Log.d("switch", "Checked 8");
                    txt = 'H';
                } else {
                    bt8 = "OFF";
                    changeColorFalse(bico8);
                    Log.d("switch", "Not Checked 8");
                    txt = 'h';
                }

                gravaValores();

                String s = String.valueOf(txt);

                /*Verifica se esta conectado */
                if (conectado) {
                    connectedThread.enviar(s);
                } else {
                    Toast.makeText(getApplicationContext(), "Bluetooth não está conectado!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        bico9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (bt9 == "OFF") {
                    bt9 = "ON";
                    changeColorTrue(bico9);
                    Log.d("switch", "Checked 9");
                    txt = 'I';
                } else {
                    bt9 = "OFF";
                    changeColorFalse(bico9);
                    Log.d("switch", "Not Checked 9");
                    txt = 'i';
                }

                gravaValores();

                String s = String.valueOf(txt);

                /*Verifica se esta conectado */
                if (conectado) {
                    connectedThread.enviar(s);
                } else {
                    Toast.makeText(getApplicationContext(), "Bluetooth não está conectado!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        bico10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (bt10 == "OFF") {
                    bt10 = "ON";
                    changeColorTrue(bico10);
                    Log.d("switch", "Checked 10");
                    txt = 'J';
                } else {
                    bt10 = "OFF";
                    changeColorFalse(bico10);
                    Log.d("switch", "Not Checked 10");
                    txt = 'j';
                }

                gravaValores();

                String s = String.valueOf(txt);

                /*Verifica se esta conectado */
                if (conectado) {
                    connectedThread.enviar(s);
                } else {
                    Toast.makeText(getApplicationContext(), "Bluetooth não está conectado!", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }


    /* Funcao do android que e chamada quando uma nova tela/janela e chamada e a mesma retorna um inteiro (startActivityForResult) */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        /*Verifica qual requisicao foi feita e o valor retornado */
        switch (requestCode) {

            case SOLICITA_ATIVACAO_BT:
                if (resultCode == Activity.RESULT_OK) {
                    /*Se o usuario permitiu a ativacao do Bluetooth */
                    Toast.makeText(getApplicationContext(), "Bluetooth ATIVO!", Toast.LENGTH_SHORT).show();
                } else {
                    /*Se o usuario nao permitiu ativar o bluetooth */
                    Toast.makeText(getApplicationContext(), "Bluetooth DESATIVADO! O app será encerrado!", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;

            case SOLICITA_CONEXAO_BT:
                if (resultCode == Activity.RESULT_OK) {
                    /* Caso tenha recebido o endereco MAC para realizar a conexao */
                    /* Salva o MAC retornado */
                    MAC = data.getExtras().getString(ListaDispositivosBluetooth.ENDERECO_MAC);
                    /* Cria um dispositivo com o endereco retornado */
                    meuDevice = meuBluetoothAdapter.getRemoteDevice(MAC);

                    /* Tenta criar um socket para comunicacao */
                    try{
                        /* Cria um socket utilizando o dispositivo externo passando o UUID desejado (SERIAL) */
                        meuSocket = meuDevice.createRfcommSocketToServiceRecord(UUID_SERIAL_PORT);

                        /* Tenta estabilizar a conexao */
                        meuSocket.connect();
                        conectado = true;


                        /*Cria uma thread */
                        connectedThread = new ConnectedThread(meuSocket);
                        connectedThread.start();

                        Toast.makeText(getApplicationContext(), "Conectado !", Toast.LENGTH_SHORT).show();

                    } catch (IOException erro) {
                        /* Caso ocorra algum problema durante a conexao */
                        conectado = false;
                        Toast.makeText(getApplicationContext(), "Ocorreu um erro ao tentar conectar"+"\n"+"ERRO :"+erro,Toast.LENGTH_LONG).show();
                    }

                } else {
                    /* Caso nenhum endereco MAC tenha sido retornado */
                    Toast.makeText(getApplicationContext(), "Falha ao retornar o endereço MAC do dispositivo selecionado.", Toast.LENGTH_SHORT).show();
                }


        }

    }

    private void changeColorTrue(Button b){
        b.setBackgroundColor(Color.parseColor("#427041"));
    }

    private void changeColorFalse(Button b){
        b.setBackgroundColor(Color.parseColor("#A9B8B8B8"));
    }

    private void changeColorAllTrue(){
        bico1.setBackgroundColor(Color.parseColor("#427041"));
        bico2.setBackgroundColor(Color.parseColor("#427041"));
        bico3.setBackgroundColor(Color.parseColor("#427041"));
        bico4.setBackgroundColor(Color.parseColor("#427041"));
        bico5.setBackgroundColor(Color.parseColor("#427041"));
        bico6.setBackgroundColor(Color.parseColor("#427041"));
        bico7.setBackgroundColor(Color.parseColor("#427041"));
        bico8.setBackgroundColor(Color.parseColor("#427041"));
        bico9.setBackgroundColor(Color.parseColor("#427041"));
        bico10.setBackgroundColor(Color.parseColor("#427041"));
    }

    private void changeColorAllFalse(){
        bico1.setBackgroundColor(Color.parseColor("#A9B8B8B8"));
        bico2.setBackgroundColor(Color.parseColor("#A9B8B8B8"));
        bico3.setBackgroundColor(Color.parseColor("#A9B8B8B8"));
        bico4.setBackgroundColor(Color.parseColor("#A9B8B8B8"));
        bico5.setBackgroundColor(Color.parseColor("#A9B8B8B8"));
        bico6.setBackgroundColor(Color.parseColor("#A9B8B8B8"));
        bico7.setBackgroundColor(Color.parseColor("#A9B8B8B8"));
        bico8.setBackgroundColor(Color.parseColor("#A9B8B8B8"));
        bico9.setBackgroundColor(Color.parseColor("#A9B8B8B8"));
        bico10.setBackgroundColor(Color.parseColor("#A9B8B8B8"));
    }

    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            meuSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run(){
            byte[] buffer = new byte[1024];
            int bytes;
            String message = "";
            String temp;

            while(true){
                try{
                    bytes = mmInStream.read(buffer);
                    temp = new String(buffer, 0, bytes);

                    message = message + temp;

                    if(message.length() == 20){
                        meuHandler.obtainMessage(MENSAGEM_RECEBIDA_BT,bytes,-1,message).sendToTarget();
                        message = "";
                    }else if(message.length() > 20){
                        message = "";
                    }


                    //Log.d("INPUT: ", String.valueOf(bytes));

                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void enviar (String dado) {
            byte[] msgBuffer = dado.getBytes();
            try {
                mmOutStream.write(msgBuffer);
            } catch (IOException e) { }
        }

    }

    private boolean isExternalStorageWritable(){
        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
            Log.i("State","Yes, it is writable!");
            return true;
        }else{
            return false;
        }
    }

    public void gravaValores(){
        currenTime = Calendar.getInstance().getTime();
        dateFormat = new SimpleDateFormat("dd/mm/yyyy HH:mm:ss");
        output_valores = output_valores + dateFormat.format(currenTime) + " " + P1 + "; " + P2 + "; " + V1 + "; " + V2 + "; " + bt1 + "; " + bt2 + "; " + bt3 + "; " + bt4 + "; " + bt5 + "; " + bt6 + "; " + bt7 + "; " + bt8 + "; " + bt9 + "; " + bt10 + "\n";
    }

    public void writeFile(String output_text, String file_name){
        File file = new File(path,file_name);

        if(isExternalStorageWritable()){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            if(checkPermission(WRITE_EXTERNAL_STORAGE)) {

                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(file);
                    fos.write(output_text.getBytes());
                    fos.close();

                    Toast.makeText(this, "File Saved.", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                Toast.makeText(this, "Permission was not granted.", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "External Storage is not Writable.", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean checkPermission(String permission){
        int check = ContextCompat.checkSelfPermission(this, permission);

        return (check == PackageManager.PERMISSION_GRANTED);
    }



}
