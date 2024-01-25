package es.riberadeltajo.buscaminas;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private int imagenMinaSeleccionada = R.drawable.bomba;

    private class MiAdaptadorMinas extends ArrayAdapter<Mina> {
        Mina[] misObjectos;

        public MiAdaptadorMinas(@NonNull Context context, int resource, @NonNull Mina[] objects) {
            super(context, resource, objects);
            misObjectos = objects;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            return crearFila(position, convertView, parent);
        }

        public View crearFila(int position, View convertView, ViewGroup parent) {
            LayoutInflater miInflador = getLayoutInflater();
            View mivista = miInflador.inflate(R.layout.minas, parent, false);
            TextView nombreMina = mivista.findViewById(R.id.nombre_mina);
            ImageView imagen = mivista.findViewById(R.id.imagen_mina);

            nombreMina.setText(misObjectos[position].getNombre());
            imagen.setImageResource(misObjectos[position].getImagen());

            return mivista;
        }
    }


    private int numFilas = 8;
    private int numColumnas = 8;
    int cantidadMinas = 10;
    int minasrestantes = cantidadMinas;

    private int[][] matriz;
    private Button[][] matrizButtons;
    private ImageButton[][] matrizImageButtons;
    private boolean[][] casillasVisitadas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ConstraintLayout c = findViewById(R.id.layout_principal);

        matrizButtons = new Button[numFilas][numColumnas];
        matrizImageButtons = new ImageButton[numFilas][numColumnas];
        casillasVisitadas = new boolean[numFilas][numColumnas];

        generarMatrizAleatoria();
        calcularMinasAlrededor();


        c.post(new Runnable() {
            @Override
            public void run() {
                iniciar();
            }
        });

    }

    public void iniciar() {
        // Calcula la altura y anchura del layout
        GridLayout g = new GridLayout(getApplicationContext());
        g.removeAllViews();
        ConstraintLayout constraintLayout = findViewById(R.id.layout_principal);

        ConstraintLayout c = findViewById(R.id.layout_principal);
        c.removeAllViews();

        int height = constraintLayout.getHeight();
        int width = constraintLayout.getWidth();

        // Parámetros para la cuadrícula
        GridLayout.LayoutParams gridParams = new GridLayout.LayoutParams();
        gridParams.setMargins(0, 0, 0, 0);
        gridParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        gridParams.width = ViewGroup.LayoutParams.MATCH_PARENT;

        // Establece las filas y columnas en la cuadrícula
        g.setRowCount(numFilas);
        g.setColumnCount(numColumnas);
        g.setLayoutParams(gridParams);

        // Calcula el tamaño de cada botón
        int buttonWidth = width / numColumnas;
        int buttonHeight = height / numFilas;

        // Parámetros para los botones
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(buttonWidth, buttonHeight);
        buttonParams.setMargins(0, 0, 0, 0);

        for (int fila = 0; fila < numFilas; fila++) {
            for (int columna = 0; columna < numColumnas; columna++) {

                int numero = matriz[fila][columna];

                View view = new View(getApplicationContext());

                if (matriz[fila][columna] == -1) {//Creacion de los imageButton
                    view = new ImageButton(getApplicationContext());
                    ImageButton imagebutton = (ImageButton) view;

                    matrizImageButtons[fila][columna] = imagebutton;

                    imagebutton.setLayoutParams(buttonParams);

                    //imagebutton.setText("");

                    imagebutton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            imagebutton.setImageResource(imagenMinaSeleccionada);

                            imagebutton.setEnabled(false);

                            dialogoHasPerdido();

                        }
                    });

                    imagebutton.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            ImageButton imagebutton = (ImageButton) view;
                            imagebutton.setImageResource(R.drawable.bandera);
                            imagebutton.setEnabled(false);
                            if (numero == -1) {
                                minasrestantes--;
                            }

                            if (minasrestantes == 0) {
                                dialogoHasGanado();
                            }
                            return true;
                        }
                    });

                    g.addView(view);

                } else {//Creacion de los button

                    view = new Button(getApplicationContext());

                    Button button = (Button) view;

                    matrizButtons[fila][columna] = button;

                    button.setLayoutParams(buttonParams);

                    button.setText("");

                    int finalFila = fila;
                    int finalColumna = columna;
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            button.setText(numero + "");

                            button.setBackgroundColor(Color.GREEN);

                            button.setEnabled(false);

                            if (numero == 0) {
                                recursividad(finalFila, finalColumna);
                            }

                        }
                    });

                    button.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            dialogoHasPerdido();
                            return true;
                        }
                    });

                    g.addView(button);
                }
                colorboton(view, fila, columna);
            }
        }

        constraintLayout.addView(g);
    }

    private void dialogoHasGanado() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        String ganar = "Has ganado la partida, prueba otra dificultad!";

        builder.setMessage(ganar);

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                reiniciar();
            }
        });

        builder.create().show();

    }

    private void dialogoHasPerdido() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        String perder = "Has perdido, empieza de nuevo";

        builder.setMessage(perder);

        for (int Fila = 0; Fila < numFilas; Fila++) {
            for (int Columna = 0; Columna < numColumnas; Columna++) {
                if (matriz[Fila][Columna] == -1) {
                    matrizImageButtons[Fila][Columna].setImageResource(R.drawable.bomba);
                }
            }
        }

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                reiniciar();
            }
        });

        builder.create().show();

    }

    private void colorboton(View view, int fila, int columna) {
        if (fila % 2 == 0) {
            if (columna % 2 == 0) {
                view.setBackgroundColor(Color.BLACK);
            } else {
                view.setBackgroundColor(Color.WHITE);
            }
        } else {
            if (columna % 2 == 0) {
                view.setBackgroundColor(Color.WHITE);
            } else {
                view.setBackgroundColor(Color.BLACK);
            }
        }
    }


    private void recursividad(int fila, int columna) {
        if (fila >= 0 && fila < numFilas && columna >= 0 && columna < numColumnas && !casillasVisitadas[fila][columna]) {
            casillasVisitadas[fila][columna] = true;

            int numero = matriz[fila][columna];

            Button casilla = matrizButtons[fila][columna];

            if (numero == 0) {
                casilla.setText(numero + "");
                casilla.setBackgroundColor(Color.GREEN);

                casilla.setEnabled(false);

                recursividad(fila - 1, columna);
                recursividad(fila + 1, columna);
                recursividad(fila, columna - 1);
                recursividad(fila, columna + 1);
                recursividad(fila - 1, columna - 1);
                recursividad(fila - 1, columna + 1);
                recursividad(fila + 1, columna - 1);
                recursividad(fila + 1, columna + 1);

            } else if (numero > 0) {

                casilla.setText(numero + "");
                casilla.setBackgroundColor(Color.GREEN);

                casilla.setEnabled(false);
            }
        }
    }


    public void generarMatrizAleatoria() {
        matriz = new int[numFilas][numColumnas];

        for (int Fila = 0; Fila < numFilas; Fila++) {
            for (int Columna = 0; Columna < numColumnas; Columna++) {
                matriz[Fila][Columna] = 0;

            }
        }

        Random random = new Random();
        int minasGeneradas = 0;

        while (minasGeneradas < cantidadMinas) {
            int randomFila = random.nextInt(numFilas);
            int randomColumna = random.nextInt(numColumnas);

            if (matriz[randomFila][randomColumna] != -1) {
                matriz[randomFila][randomColumna] = -1;
                minasGeneradas++;
            }
        }
        for (int fila = 0; fila < numFilas; fila++) {
            Log.d("MatrizGenerada", Arrays.toString(matriz[fila]));
        }

    }

    public void calcularMinasAlrededor() {


        for (int Fila = 0; Fila < numFilas; Fila++) {
            for (int Columna = 0; Columna < numColumnas; Columna++) {
                if (matriz[Fila][Columna] != -1) {
                    int minasCercanas = 0;

                    for (int i = -1; i <= 1; i++) {
                        for (int j = -1; j <= 1; j++) {
                            int nuevaFila = Fila + i;
                            int nuevaColumna = Columna + j;

                            if (nuevaFila >= 0 && nuevaFila < numFilas && nuevaColumna >= 0 && nuevaColumna < numColumnas) {
                                if (matriz[nuevaFila][nuevaColumna] == -1) {
                                    minasCercanas++;
                                }
                            }
                        }
                    }

                    matriz[Fila][Columna] = minasCercanas;
                }
            }
        }
        for (int fila = 0; fila < numFilas; fila++) {
            Log.d("MatrizMinasCercanas", Arrays.toString(matriz[fila]));
        }

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.seleccion_pers) {
            dialogominas();
        } else if (item.getItemId() == R.id.dificultad) {
            mostrarDialogoDificultad();
        } else if (item.getItemId() == R.id.reiniciar) {
            reiniciar();
        } else {
            dialogoInstrucciones();
        }
        return super.onOptionsItemSelected(item);
    }

    private void dialogominas() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecciona la mina que más te guste");

        ListView listaMinas = new ListView(this);
        Mina[] arrayMinas = new Mina[5];
        arrayMinas[0] = new Mina("Mina predeterminada", R.drawable.bomba);
        arrayMinas[1] = new Mina("Mina minecraft", R.drawable.creeper_bomba);
        arrayMinas[2] = new Mina("Mina angry birds", R.drawable.cerdo_bomba);
        arrayMinas[3] = new Mina("Mina mario bros", R.drawable.bomba_mario);
        arrayMinas[4] = new Mina("Mina kirby", R.drawable.bomba_kirby);
        MiAdaptadorMinas miAdaptador = new MiAdaptadorMinas(this, R.layout.minas, arrayMinas);

        listaMinas.setAdapter(miAdaptador);

        builder.setView(listaMinas);

        listaMinas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                imagenMinaSeleccionada = arrayMinas[position].getImagen();
                Toast.makeText(MainActivity.this, "Imagen de mina seleccionada", Toast.LENGTH_SHORT).show();

                for (int Fila = 0; Fila < numFilas; Fila++) {
                    for (int Columna = 0; Columna < numColumnas; Columna++) {
                        if (matriz[Fila][Columna] == -1 && matrizImageButtons[Fila][Columna] != null) {
                            matrizImageButtons[Fila][Columna].setImageResource(imagenMinaSeleccionada);
                        }
                    }
                }
            }
        });


        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                iniciar();
            }
        });


        builder.show();
    }


    private void dialogoInstrucciones() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Instrucciones del juego");

        String instrucciones = "Cuando pulsas en una casilla, sale un número que identifica cuántas minas hay alrededor. Ten cuidado porque si pulsas en una casilla que tenga una mina escondida, perderás. Si crees o tienes la certeza de que hay una mina, haz un click largo sobre la casilla para señalarla. No hagas un click largo en una casilla donde no hay una mina porque perderás. Ganas una vez hayas encontrado todas las minas";

        builder.setMessage(instrucciones);

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    private void reiniciar() {

        matriz = new int[numFilas][numColumnas];
        matrizButtons = new Button[numFilas][numColumnas];
        casillasVisitadas = new boolean[numFilas][numColumnas];

        generarMatrizAleatoria();
        calcularMinasAlrededor();

        iniciar();
    }

    private void mostrarDialogoDificultad() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecciona la dificultad");
        String[] dificultades = {"Fácil", "Intermedia", "Difícil"};

        builder.setItems(dificultades, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String dificultadSeleccionada = dificultades[which];
                // Realiza acciones según la dificultad seleccionada
                if (dificultadSeleccionada.equals("Fácil")) {
                    cambiarDificultad(8, 8, 10);
                } else if (dificultadSeleccionada.equals("Intermedia")) {
                    cambiarDificultad(12, 12, 30);
                } else if (dificultadSeleccionada.equals("Difícil")) {
                    cambiarDificultad(16, 16, 60);
                }


                Toast.makeText(MainActivity.this, "Dificultad seleccionada: " + dificultadSeleccionada, Toast.LENGTH_SHORT).show();

                iniciar();
            }
        });

        builder.show();
    }

    public void cambiarDificultad(int nuevasFilas, int nuevasColumnas, int nuevasMinas) {
        numFilas = nuevasFilas;
        numColumnas = nuevasColumnas;
        cantidadMinas = nuevasMinas;
        matrizButtons = new Button[numFilas][numColumnas];
        matrizImageButtons = new ImageButton[numFilas][numColumnas];
        casillasVisitadas = new boolean[numFilas][numColumnas];


        generarMatrizAleatoria();
        calcularMinasAlrededor();

        iniciar();
    }

}