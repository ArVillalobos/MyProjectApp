package com.project.myprojectapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var productoAdapter: ProductoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val btnNavegar: Button = findViewById(R.id.btnNavegar)
        btnNavegar.setOnClickListener {
            val intent = Intent(this, AdminProductoActivity::class.java)
            startActivity(intent)
        }

        val btnListar: Button = findViewById(R.id.btnListar)
        btnListar.setOnClickListener {
            obtenerProductos()
        }
    }

    private fun obtenerProductos() {
        val db = FirebaseFirestore.getInstance()
        db.collection("Productos")
            .get()
            .addOnSuccessListener { result ->
                val productos = mutableListOf<Producto>()
                for (document in result) {
                    val producto = document.toObject(Producto::class.java)
                    productos.add(producto)
                }
                productoAdapter = ProductoAdapter(productos)
                recyclerView.adapter = productoAdapter
            }
            .addOnFailureListener { exception ->
                Log.w("MainActivity", "Error getting documents: ", exception)
            }
    }
}