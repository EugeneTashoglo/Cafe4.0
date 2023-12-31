package com.example.cafe40

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class CoffeeDetailActivity : AppCompatActivity() {
    private lateinit var coffeeName: TextView
    private lateinit var adapter: ChatAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var coffeeImage: ImageView
    private lateinit var coffeeDescription: TextView
    private lateinit var chatInput: EditText
    private lateinit var sendButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coffee_detail)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        recyclerView = findViewById(R.id.chatRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        coffeeName = findViewById(R.id.coffeeName)
        coffeeImage = findViewById(R.id.coffeeImage)
        coffeeDescription = findViewById(R.id.coffeeDescription)
        chatInput = findViewById(R.id.chatInput)
        sendButton = findViewById(R.id.sendButton)
        adapter = ChatAdapter()
        recyclerView.adapter = adapter
        val name = intent.getStringExtra("COFFEE_NAME")
        val description = intent.getStringExtra("COFFEE_DESCRIPTION")
        val img = intent.getStringExtra("COFFEE_IMAGE")
        Picasso.get().load(img).into(coffeeImage)
        coffeeName.text = name
        coffeeDescription.text = description
        val coffeeName = intent.getStringExtra("COFFEE_NAME")
        if (coffeeName != null) {
            loadChatMessages(coffeeName)
            sendButton.setOnClickListener {
                sendChatMessage(coffeeName, chatInput.text.toString())
                chatInput.text.clear()
            }
        } else {
            Log.d("CoffeeDetailActivity", "No coffee name passed in intent")
        }
    }



    private fun loadChatMessages(coffeeName: String?) {
        val database = FirebaseDatabase.getInstance()
        val chatRef = database.getReference("Chats").child(coffeeName ?: return)

        chatRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val chatList =
                    dataSnapshot.children.mapNotNull { it.getValue(ChatMessage::class.java) }
                adapter.submitList(chatList)
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("CoffeeDetailActivity", "Failed to load chat messages", error.toException())
            }

        })
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun sendChatMessage(coffeeName: String?, message: String) {
        val database = FirebaseDatabase.getInstance()
        val chatRef = database.getReference("Chats").child(coffeeName ?: return)
        val sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "") ?: ""
        val chatMessage = ChatMessage(sender = username, message = message)
        chatRef.push().setValue(chatMessage)
    }

}