package noahandrews.me.pstester

import android.annotation.SuppressLint
import android.content.pm.ApplicationInfo
import android.os.Build
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val DEBUG_TEXT: Spanned = Html.fromHtml("This app is <b>debuggable.</b>")
    private val RELEASE_TEXT : Spanned = Html.fromHtml("This app is <b>not debuggable.</b>")

    val commandRunner = RunShellCommand()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var versionString = ""

        when(VERSION.SDK_INT) {
            VERSION_CODES.KITKAT -> versionString = "KitKat 4.4"
            VERSION_CODES.KITKAT_WATCH -> versionString = "KitKat 4.4 for watches"
            VERSION_CODES.LOLLIPOP -> versionString = "Lollipop 5.0"
            VERSION_CODES.LOLLIPOP_MR1 -> versionString = "Lollipop 5.1"
            VERSION_CODES.M -> versionString = "Marshmallow 6.0"
            VERSION_CODES.N -> versionString = "Nougat 7.0"
            VERSION_CODES.N_MR1 -> versionString = "Nougat 7.1"
            VERSION_CODES.O -> versionString = "Oreo 8.0"
            VERSION_CODES.O_MR1 -> versionString = "Oreo 8.1"
        }
        if(versionString.isEmpty()) {
            versionString = "Android version from the future"
        }

        apiLevelTextView.text = "API level: ${VERSION.SDK_INT}\n$versionString"
        manufacturerTextView.text = "Manufacturer: ${Build.MANUFACTURER}"
        modelTextView.text = "Model: ${Build.MODEL}"

        if((applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
            modeTextView.text = DEBUG_TEXT
        } else {
            modeTextView.text = RELEASE_TEXT
        }

        refreshPsOutput()

        spawnLogcatButton.setOnClickListener {
            Thread {
                commandRunner.run("logcat")
            }.start()
            refreshPsOutput()
        }

        killLogcatButton.setOnClickListener {
            RunShellCommand.killSpawnedProcess("logcat", packageName, commandRunner)
            refreshPsOutput()
        }



    }

    private fun refreshPsOutput() {
        val psOutput = commandRunner.run("ps") ?: ""
        var psOutputUsers = ""
        var psOutputNames = ""
        psOutput.split("\n")/*.drop(1)*/
                .asSequence()
                .map { it.split("\\s+".toRegex()) }
                .forEach {
                    psOutputUsers += "${it[0]}\n"
                    psOutputNames += "${it[it.size - 1]}\n"
                }


        psOutputUsersTextView.text = psOutputUsers
        psOutputNamesTextView.text = psOutputNames
        psOutputNamesTextView.setHorizontallyScrolling(true)
    }
}
