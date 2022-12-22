package com.example.itextpdf_kotlin

import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.itextpdf_kotlin.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    var listaUsuarios: ArrayList<Usuario> = arrayListOf()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isAceptado ->
        if (isAceptado) Toast.makeText(this, "PERMISOS CONCEDIDOS", Toast.LENGTH_SHORT).show()
        else Toast.makeText(this, "PERMISOS DENEGADOS", Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listaUsuarios.add(Usuario("xcheko51x", "Sergio Peralta", "sergiop@local.com"))
        listaUsuarios.add(Usuario("laurap", "Laura Perez", "laurap@local.com"))
        listaUsuarios.add(Usuario("juanm", "Juan Morales", "juanm@local.com"))

        binding.btnCrearPdf.setOnClickListener {
            verificarPermisos(it)
        }

    }

    private fun crearPDF() {
        try {
            val carpeta = "/archivospdf"
            val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath + carpeta

            val dir = File(path)
            if (!dir.exists()) {
                dir.mkdirs()
                Toast.makeText(this, "CARPETA CREADA", Toast.LENGTH_SHORT).show()
            }

            val file = File(dir, "usuarios.pdf")
            val fileOutputStream = FileOutputStream(file)

            val documento = Document()
            PdfWriter.getInstance(documento, fileOutputStream)

            documento.open()

            val titulo = Paragraph(
                "Lista de Usuarios \n\n\n",
                FontFactory.getFont("arial", 22f, Font.BOLD, BaseColor.BLUE)
            )

            documento.add(titulo)

            val tabla = PdfPTable(3)
            tabla.addCell("USUARIO")
            tabla.addCell("NOMBRE")
            tabla.addCell("CORREO")

            for (i in listaUsuarios.indices) {
                tabla.addCell(listaUsuarios[i].usuario)
                tabla.addCell(listaUsuarios[i].nombre)
                tabla.addCell(listaUsuarios[i].email)
            }

            documento.add(tabla)

            documento.close()


        } catch (e: FileNotFoundException) {
            e.printStackTrace();
        } catch (e: DocumentException) {
            e.printStackTrace()
        }
    }

    private fun verificarPermisos(view: View) {
        when {
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                Toast.makeText(this, "PERMISOS CONCEDIDOS", Toast.LENGTH_SHORT).show()
                crearPDF()
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) -> {
                Snackbar.make(
                    view,
                    "ESTE PERMISO ES NECESARIO PARA CREAR EL ARCHIVO",
                    Snackbar.LENGTH_INDEFINITE
                ).setAction("OK") {
                    requestPermissionLauncher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }.show()
            }

            else -> {
                requestPermissionLauncher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }

    }
}