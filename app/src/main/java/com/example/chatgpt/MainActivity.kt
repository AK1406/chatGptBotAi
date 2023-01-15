package com.example.chatgpt

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.chatgpt.ExtensionFunctionUtils.afterTextChanged
import com.example.chatgpt.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList


open class MainActivity : AppCompatActivity(), APIListener {
    val TAG = "MainActivity"
    var resList: ArrayList<Choices> = arrayListOf()
    lateinit var binding: ActivityMainBinding
    var action: String = ""
    private val REQUEST_CODE_SPEECH_INPUT = 1
    val languageList = arrayOf("hindi", "english", "spanish", "chinese")
    var lang: String = ""
    var chatList: ArrayList<ChatModel> = arrayListOf()
    var mAdapter: ChatAdapter? = null
    var wantChat: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setListener()
        binding.ivChat.setColorFilter(
            ContextCompat.getColor(this, R.color.black),
            android.graphics.PorterDuff.Mode.SRC_IN
        )
        binding.tvgptAns.hint = "Let's chat"
        /*
        binding.llChatContainer.visibility = View.VISIBLE
        binding.llChatContainer.visibility = View.GONE*/
        setChatAdapter()
        selectOption()
        customBotMessage("Hello! you're speaking with Chat GPT, how may I help?")
    }

    override fun onStart() {
        super.onStart()
        //In case there are messages, scroll to bottom when re-opening app
        GlobalScope.launch {
            delay(100)
            withContext(Dispatchers.Main) {
                binding.rvChatList.scrollToPosition(mAdapter?.itemCount?.minus(1) ?: 0)
            }
        }
    }

    fun setListener() {
        binding.etWrite.afterTextChanged {
            if (binding.etWrite.text.trim().isNotBlank()) {
                val request =
                    GptRequest("text-davinci-003", action + binding.etWrite.text.toString().trim())
                val msg = action + binding.etWrite.text.toString().trim()
                chatList.add(ChatModel(msg, "SEND_ID", null))
                binding.etWrite.setText("")
                if (wantChat) {
                    binding.laProgressAnimation.visibility = View.GONE
                    mAdapter?.insertMessage(ChatModel(msg, "SEND_ID", null))
                    binding.rvChatList.scrollToPosition(mAdapter?.itemCount?.minus(1) ?: 0)
                }
                sendMsg(request)
            }
        }
        //Scroll back to correct position when user clicks on text view
        binding.etWrite.setOnClickListener {
            GlobalScope.launch {
                delay(100)
                withContext(Dispatchers.Main) {
                    binding.rvChatList.scrollToPosition(mAdapter?.itemCount?.minus(1) ?: 0)

                }
            }
        }
        binding.ivMic.setOnClickListener {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault()
            )
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text")

            try {
                startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
            } catch (e: Exception) {
                Toast
                    .makeText(
                        this@MainActivity, " " + e.message,
                        Toast.LENGTH_SHORT
                    )
                    .show()
            }
        }
    }

    override fun onActivityResult(
        requestCode: Int, resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && data != null) {
                val result = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS
                )
                binding.etWrite.setText(
                    Objects.requireNonNull(result)?.get(0) ?: "hello"
                )
            }
        }
    }

    private fun sendMsg(request: GptRequest) {
        Log.i(TAG, "sendMsg($request)")
        var ans: String? = ""
        if (!wantChat) {
            showProgress()
        }
        RetrofitClient.api.getGptAnswer(request)
            .enqueue(object : Callback<GptResponse> {
                override fun onResponse(call: Call<GptResponse>, response: Response<GptResponse>) {
                    // Handle the response
                    Log.i(TAG, "onResponse: $response")
                    if (response.isSuccessful) {
                        hideProgress()
                        Log.i(TAG, "success")
                        val gpt = response.body()
                        resList.clear()
                        gpt?.choices?.let { resList.addAll(it) }
                        Log.i("MainActivity", "list: ${resList}")
                        ans = resList[0].text
                        ans = ans.toString().trim()
                        Log.i(TAG, "ans: $ans")
                        if (!wantChat) {
                            binding.tvgptAns.text = ans
                        } else {
                            //Adds it to our local list
                            chatList.add(ChatModel(ans!!, "RECEIVE_ID", null))
                            //Inserts our message into the adapter
                            mAdapter?.insertMessage(ChatModel(ans, "RECEIVE_ID", null))
                            //Scrolls us to the position of the latest message
                            binding.rvChatList.scrollToPosition(mAdapter?.itemCount?.minus(1) ?: 0)
                        }
                    } else {
                        Log.i(
                            TAG,
                            "errorBody: ${response.errorBody()}, code: ${response.code()}, message: ${response.message()}, headers: ${response.headers()}, raw: ${response.raw()}, body: ${response.body()}"
                        )
                    }
                }

                override fun onFailure(call: Call<GptResponse>, t: Throwable) {
                    // Handle the error
                    if (!wantChat) {
                        hideProgress()
                    }
                    Log.i(TAG, "failure")
                    Log.i(TAG, "$t")
                    ans = t.toString()
                }
            })
    }

    private fun customBotMessage(message: String) {

        GlobalScope.launch {
            delay(1000)
            withContext(Dispatchers.Main) {
                // val timeStamp = Time.timeStamp()
                chatList.add(ChatModel(message, "RECEIVE_ID", null))
                mAdapter?.insertMessage(ChatModel(message, "RECEIVE_ID", null))

                binding.rvChatList.scrollToPosition(mAdapter?.itemCount?.minus(1) ?: 0)
            }
        }
    }

    override fun showProgress() {
        showProgress(this)
    }

    override fun hideProgress() {
        // mProgressDialog?.dismiss()
        binding.laProgressAnimation.visibility = View.GONE
    }

    override fun networkError() {
        SnackBarUtils.showCustomSnackBar(binding.root, "Please check your network", true)
    }


    protected fun showProgress(context: Context?, message: String = "Please wait...") {
        binding.tvgptAns.text = ""
        binding.laProgressAnimation.visibility = View.VISIBLE
        binding.laProgressAnimation.playAnimation()
        binding.laProgressAnimation.repeatCount = Animation.INFINITE

        /*mProgressDialog?.dismiss()
        mProgressDialog = ProgressDialog.getProgressDialog(this, message)
        mProgressDialog!!.setCancelable(false)
        mProgressDialog!!.setCanceledOnTouchOutside(false)
        mProgressDialog!!.show()*/
    }

    fun noChat() {
        binding.llChatContainer.visibility = View.GONE
        binding.llGtpAns.visibility = View.VISIBLE
        wantChat = false
        // binding.laProgressAnimation.visibility = View.VISIBLE
    }

    fun selectOption() {
        binding.flQuestion.setOnClickListener {
            noChat()
            binding.ivQuestion.setColorFilter(
                ContextCompat.getColor(this, R.color.black),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
            binding.ivChat.setColorFilter(
                ContextCompat.getColor(this, R.color.white),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
            binding.ivTranslate.setColorFilter(
                ContextCompat.getColor(this, R.color.white),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
            binding.ivGrammarCheck.setColorFilter(
                ContextCompat.getColor(this, R.color.white),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
/*
            binding.llChatContainer.visibility = View.GONE
            binding.llChatContainer.visibility = View.VISIBLE*/
            binding.tvgptAns.text = ""
            binding.tvgptAns.hint = "Ask me anything..."
            action = ""
        }

        binding.flChat.setOnClickListener {
            binding.llChatContainer.visibility = View.VISIBLE
            binding.llGtpAns.visibility = View.GONE
            binding.laProgressAnimation.visibility = View.GONE
            wantChat = true
            binding.ivChat.setColorFilter(
                ContextCompat.getColor(this, R.color.black),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
            binding.ivQuestion.setColorFilter(
                ContextCompat.getColor(this, R.color.white),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
            binding.ivTranslate.setColorFilter(
                ContextCompat.getColor(this, R.color.white),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
            binding.ivGrammarCheck.setColorFilter(
                ContextCompat.getColor(this, R.color.white),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
/*
            binding.llChatContainer.visibility = View.VISIBLE
            binding.llChatContainer.visibility = View.GONE*/
            binding.tvgptAns.text = ""
            binding.tvgptAns.hint = "Let's chat..."
            action = ""
        }

        binding.flTranslate.setOnClickListener {
            noChat()
            initSpinner()
            binding.spLanguage.performClick()
            binding.tvgptAns.text = ""
        }

        binding.flGrammarCheck.setOnClickListener {
            noChat()
            binding.ivChat.setColorFilter(
                ContextCompat.getColor(this, R.color.white),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
            binding.ivQuestion.setColorFilter(
                ContextCompat.getColor(this, R.color.white),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
            binding.ivTranslate.setColorFilter(
                ContextCompat.getColor(this, R.color.white),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
            binding.ivGrammarCheck.setColorFilter(
                ContextCompat.getColor(this, R.color.black),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
/*
            binding.llChatContainer.visibility = View.GONE
            binding.llChatContainer.visibility = View.VISIBLE*/
            binding.tvgptAns.text = ""
            binding.tvgptAns.hint = "Grammar/Spelling check... "
            action = "Check grammar and spelling mistake in "
        }
    }

    fun initSpinner() {
        binding.spLanguage.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            languageList
        )
        binding.spLanguage.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    //
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    lang = parent?.getItemAtPosition(position).toString().trim()
                    noChat()
                    binding.ivChat.setColorFilter(
                        ContextCompat.getColor(this@MainActivity, R.color.white),
                        android.graphics.PorterDuff.Mode.SRC_IN
                    )
                    binding.ivQuestion.setColorFilter(
                        ContextCompat.getColor(this@MainActivity, R.color.white),
                        android.graphics.PorterDuff.Mode.SRC_IN
                    )
                    binding.ivTranslate.setColorFilter(
                        ContextCompat.getColor(this@MainActivity, R.color.black),
                        android.graphics.PorterDuff.Mode.SRC_IN
                    )
                    binding.ivGrammarCheck.setColorFilter(
                        ContextCompat.getColor(this@MainActivity, R.color.white),
                        android.graphics.PorterDuff.Mode.SRC_IN
                    )/*
                    binding.llChatContainer.visibility = View.GONE
                    binding.llChatContainer.visibility = View.VISIBLE*/
                    binding.tvgptAns.hint = "Translate into $lang..."
                    action = "Translate into $lang"
                }
            }
    }

    fun setChatAdapter() {
        mAdapter = ChatAdapter(this)
        binding.rvChatList.adapter = mAdapter
    }

}