package online.a101app.a101

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

class LoginActivity: AppCompatActivity() {

    var auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        var emailField = findViewById(R.id.email_field) as EditText
        var passwordField = findViewById(R.id.password_field) as EditText
        var loginButton = findViewById(R.id.login_button) as Button

        loginButton.setOnClickListener { view ->
            signIn(view, emailField.text.toString(), passwordField.text.toString())
        }
    }

    fun signIn(view: View, email: String, password: String) {
        Log.d("TAG", "Signing in")
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, OnCompleteListener<AuthResult> { task ->
            if(task.isSuccessful){
                Log.d("TAG", "Success")
                var intent = Intent(this, MainActivity::class.java)
                intent.putExtra("id", auth.currentUser?.email)
                startActivity(intent)

            }else{
                Log.d("TAG", "Fail")

            }
        })
    }




}