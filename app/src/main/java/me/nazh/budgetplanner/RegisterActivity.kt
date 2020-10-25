package me.nazh.budgetplanner

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        register_button_register.setOnClickListener {
            performRegister()
        }

        already_have_account_text_view.setOnClickListener {
            Log.d("RegisterActivityD", "Try to show login activity")

            // launch the login activity somehow
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun performRegister(){
        val email = email_edittext_register.text.toString()
        val password = password_edittext_register.text.toString()

        if (email.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Please enter text in email and password", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("RegisterActivityD", "Email is: " + email)
        Log.d("RegisterActivityD", "Password: $password")

        //Firebase
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener

                //else if successful
                Log.d("RegisterActivityD", "Successfully created user with uid: ${it.result?.user?.uid}")
                saveUserToFirebaseDatabase()
            }
            .addOnFailureListener {
                Log.d("RegisterActivityD", "Failed to create user: ${it.message}")
                Toast.makeText(this, "Failed to create user: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveUserToFirebaseDatabase () {
        //create value with user unique ID
        val uid = FirebaseAuth.getInstance().uid ?:""
        Log.d("RegisterActivityD", "saveUserToFirebaseDatabase func, uid = $uid")

        //create value with path where to save new user in database
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        Log.d("RegisterActivityD", "saveUserToFirebaseDatabase func, ref = $ref")

        //create value with Timestamp of creation a new current user
        //val userCreationTimeStamp = FirebaseAuth.getInstance().currentUser?.metadata?.creationTimestamp
        //Log.d("RegisterActivityD", "saveUserToFirebaseDatabase func, userCreationTimeStamp = $userCreationTimeStamp")

        //transform Timestamp to Date format
        //val userCreationDate = Date(userCreationTimeStamp!!).toString()
        //Log.d("RegisterActivityD", "saveUserToFirebaseDatabase func, userCreationTimeStamp = $userCreationDate")
        //val userCreationDate = SimpleDateFormat("MM/dd/yyyy").format(Date(userCreationTimeStamp!!))
        //Log.d("RegisterActivityD", "saveUserToFirebaseDatabase func, userCreationDate = $userCreationDate")

        val user = User(usename_edittext_register.text.toString(), email_edittext_register.text.toString())
        Log.d("RegisterActivityD", "saveUserToFirebaseDatabase func, user = $user, username = ${user.username}, email = ${user.email}")

        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("RegisterActivityD", "Saved user to Firebase Database")

                val intent = Intent(this, LatestMessagesActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener {
                Log.d("RegisterActivityD","NOT saved user to Firebase Database")
                Log.d("RegisterActivityD", "Failed to save user to database: ${it.message}")
            }


    }
}

//class User(val username: String, val email: String, val creation_date: String)
class User(val username: String, val email: String)