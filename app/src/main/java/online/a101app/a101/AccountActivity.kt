package online.a101app.a101

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class AccountActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.account)
    }


    // Sign out the currently signed in user and send them to the main activity, where the firebaseUI sign in activity will launch.
    fun signOut(view: View) {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, MainActivity::class.java).apply {

        }
        startActivity(intent)
    }

    // Send a password reset email to the currently signed in user.
    fun sendPasswordReset(view: View) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(FirebaseAuth.getInstance().currentUser!!.email!!)
        Toast.makeText(this, "Password email reset sent.", Toast.LENGTH_SHORT).show()
    }
}
