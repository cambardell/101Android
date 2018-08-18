package online.a101app.a101

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ListView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ChooseSchoolActivity: AppCompatActivity() {
    var schoolList: Array<String> = arrayOf("University of Waterloo", "Queens University", "Wilfrid Laurier University", "Trent University", "Carleton University", "McMaster University")

    private var listViewItems: ListView? = null
    lateinit var adapter: SchoolAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.choose_school)

        listViewItems = findViewById<View>(R.id.schools_list) as ListView
        adapter = SchoolAdapter(this, schoolList)
        listViewItems!!.setAdapter(adapter)

        // When a row is clicked, set the users school and move to the main view
        listViewItems?.setOnItemClickListener {_, _, position, _ ->
            val selectedSchool: HashMap<String, String> = hashMapOf("school" to schoolList[position])
            val i = Intent(this, MainActivity::class.java)
            val userId = FirebaseAuth.getInstance().currentUser!!.uid

            FirebaseDatabase.getInstance().reference.child("users").child(userId).setValue(selectedSchool)
            startActivity(i)
        }
    }


}