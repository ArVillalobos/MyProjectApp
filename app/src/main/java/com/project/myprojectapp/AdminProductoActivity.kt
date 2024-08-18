package com.project.myprojectapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AdminProductoActivity : AppCompatActivity() {

    private val productoRepository = ProductoRepository()

    private lateinit var etNombre: EditText
    private lateinit var etPrecio: EditText
    private lateinit var etCantidad: EditText
    private lateinit var btnRegresar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_producto)

        // Inicializar las vistas
        etNombre = findViewById(R.id.etNombre)
        etPrecio = findViewById(R.id.etPrecio)
        etCantidad = findViewById(R.id.etCantidad)
        btnRegresar = findViewById(R.id.btnRegresar)

        // Configurar botón de regresar
        btnRegresar.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()  // Opcional, para cerrar la actividad actual
        }

        // Configurar botones para actualizar, borrar y modificar
        val btnCrear: Button = findViewById(R.id.btnCrear)
        val btnBorrar: Button = findViewById(R.id.btnBorrar)
        val btnModificar: Button = findViewById(R.id.btnModificar)


        btnCrear.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val precioTexto = etPrecio.text.toString().trim()
            val cantidadTexto = etCantidad.text.toString().trim()

            // Validar que los campos no estén vacíos
            if (nombre.isEmpty()) {
                etNombre.error = "El nombre es obligatorio"
                etNombre.requestFocus()
                return@setOnClickListener
            }

            if (precioTexto.isEmpty()) {
                etPrecio.error = "El precio es obligatorio"
                etPrecio.requestFocus()
                return@setOnClickListener
            }

            if (cantidadTexto.isEmpty()) {
                etCantidad.error = "La cantidad es obligatoria"
                etCantidad.requestFocus()
                return@setOnClickListener
            }

            // Convertir los valores de precio y cantidad a enteros
            val precio = precioTexto.toIntOrNull()
            val cantidad = cantidadTexto.toIntOrNull()

            if (precio == null || precio <= 0) {
                etPrecio.error = "Ingresa un precio válido"
                etPrecio.requestFocus()
                return@setOnClickListener
            }

            if (cantidad == null || cantidad < 0) {
                etCantidad.error = "Ingresa una cantidad válida"
                etCantidad.requestFocus()
                return@setOnClickListener
            }

            // Crear el producto solo si todos los campos son válidos
            val producto = Producto(
                nombre = nombre,
                precio = precio,
                cantidad = cantidad,
                id = ""  // El ID será asignado por Firestore
            )

            productoRepository.crearProducto(producto, {
                Toast.makeText(this, "Producto creado exitosamente", Toast.LENGTH_SHORT).show()
                // Limpiar los campos después de crear el producto
                etNombre.text.clear()
                etPrecio.text.clear()
                etCantidad.text.clear()
            }, { exception ->
                Toast.makeText(this, "Error al crear el producto: ${exception.message}", Toast.LENGTH_SHORT).show()
            })
        }

        btnBorrar.setOnClickListener {
            val nombre = etNombre.text.toString()
            productoRepository.borrarProducto(nombre, {
                Toast.makeText(this, "Producto borrado exitosamente", Toast.LENGTH_SHORT).show()
            }, { exception ->
                Toast.makeText(this, "Error al borrar el producto: ${exception.message}", Toast.LENGTH_SHORT).show()
            })
        }

        btnModificar.setOnClickListener {
            val nombre = etNombre.text.toString()
            val nuevoPrecio = etPrecio.text.toString().toIntOrNull()
            val nuevaCantidad = etCantidad.text.toString().toIntOrNull()

            productoRepository.modificarProducto(nombre, nuevoPrecio, nuevaCantidad, {
                Toast.makeText(this, "Producto modificado exitosamente", Toast.LENGTH_SHORT).show()
            }, { exception ->
                Toast.makeText(this, "Error al modificar el producto: ${exception.message}", Toast.LENGTH_SHORT).show()
            })
        }
    }
}