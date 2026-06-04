package com.example.rutaupt.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.rutaupt.model.Chofer
import com.example.rutaupt.storage.ChoferRepository

@Composable
fun ListaChoferesScreen(
    onVolver: () -> Unit
) {

    Scaffold {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {

            LazyColumn {

                items(
                    ChoferRepository.choferes
                ) { chofer ->

                    ChoferCard(chofer)
                }
            }

            Button(
                onClick = onVolver,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Regresar")
            }
        }
    }
}

@Composable
fun ChoferCard(
    chofer: Chofer
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text("${chofer.nombre} ${chofer.apellido}")

            Text("Unidad: ${chofer.numeroUnidad}")

            Text("${chofer.horaSalida} - ${chofer.horaLlegada}")

            Row {

                Button(
                    onClick = {

                    }
                ) {
                    Text("Editar")
                }

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                Button(
                    onClick = {
                        ChoferRepository.choferes.remove(
                            chofer
                        )
                    }
                ) {
                    Text("Eliminar")
                }
            }
        }
    }
}