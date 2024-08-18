package com.project.myprojectapp

import com.google.firebase.firestore.FirebaseFirestore

class ProductoRepository {

    private val db = FirebaseFirestore.getInstance()
    private val productosCollection = db.collection("Productos")

    // Método para crear un nuevo producto
    fun crearProducto(producto: Producto, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val nuevoProductoRef = productosCollection.document()
        val productoConId = producto.copy(id = nuevoProductoRef.id)

        nuevoProductoRef.set(productoConId)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    // Método para borrar un producto por nombre
    fun borrarProducto(nombre: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        productosCollection.whereEqualTo("nombre", nombre)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    onFailure(Exception("No se encontró un producto con ese nombre"))
                    return@addOnSuccessListener
                }

                for (document in documents) {
                    productosCollection.document(document.id).delete()
                        .addOnSuccessListener {
                            onSuccess()
                        }
                        .addOnFailureListener { exception ->
                            onFailure(exception)
                        }
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    // Método para modificar un producto por nombre
    fun modificarProducto(nombre: String, nuevoPrecio: Int?, nuevaCantidad: Int?, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        productosCollection.whereEqualTo("nombre", nombre)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    onFailure(Exception("No se encontró un producto con ese nombre"))
                    return@addOnSuccessListener
                }

                for (document in documents) {
                    val productoRef = productosCollection.document(document.id)

                    // Obtener los valores actuales si no se ingresan nuevos
                    val precioActual = document.getLong("precio")?.toInt() ?: 0
                    val cantidadActual = document.getLong("cantidad")?.toInt() ?: 0

                    val datosActualizados = mutableMapOf<String, Any>()

                    nuevoPrecio?.let {
                        datosActualizados["precio"] = it
                    } ?: run {
                        datosActualizados["precio"] = precioActual
                    }

                    nuevaCantidad?.let {
                        datosActualizados["cantidad"] = it
                    } ?: run {
                        datosActualizados["cantidad"] = cantidadActual
                    }

                    productoRef.update(datosActualizados)
                        .addOnSuccessListener {
                            onSuccess()
                        }
                        .addOnFailureListener { exception ->
                            onFailure(exception)
                        }
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}