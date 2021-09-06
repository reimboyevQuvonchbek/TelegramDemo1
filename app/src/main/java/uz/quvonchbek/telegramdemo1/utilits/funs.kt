package uz.quvonchbek.telegramdemo1.utilits

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import com.squareup.picasso.Picasso
import uz.quvonchbek.telegramdemo1.MainActivity
import uz.quvonchbek.telegramdemo1.R
import java.text.SimpleDateFormat
import java.util.*

fun showToast(message: String) {
    Toast.makeText(APP_ACTIVITY, message, Toast.LENGTH_SHORT).show()
}

fun reStartActivity() {
    val intent = Intent(APP_ACTIVITY, MainActivity::class.java)
    APP_ACTIVITY.startActivity(intent)
    APP_ACTIVITY.finish()
}

fun replaceFragment(
    fragment: Fragment,
    stack: Boolean = true
) {
    if (stack) {
        APP_ACTIVITY.supportFragmentManager.beginTransaction()
            .addToBackStack(null)
            .replace(R.id.main_container, fragment)
            .commit()
    } else {
        APP_ACTIVITY.supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, fragment)
            .commit()
    }

}

fun replaceFragment(
    fragment: Fragment,
    bundle: Bundle,
    stack: Boolean = true
) {
    if (stack) {
        APP_ACTIVITY.supportFragmentManager.beginTransaction().addToBackStack(null)
            .replace(R.id.main_container, fragment::class.java, bundle).commit()
    } else {
        APP_ACTIVITY.supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, fragment::class.java, bundle).commit()
    }
}


fun hideKeyboard() {
    val imm = APP_ACTIVITY.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(APP_ACTIVITY.window.decorView.windowToken, 0)
}

fun ImageView.setUriImage(url: String) {
    if (url == "") {
        Picasso.get().load(R.drawable.tom).placeholder(R.drawable.default_photo).into(this)
    } else {
        Picasso.get().load(url).placeholder(R.drawable.default_photo).fit().into(this)
    }
}
fun String.asTime(): String {
    val time = Date(this.toLong())
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    return timeFormat.format(time)
}
