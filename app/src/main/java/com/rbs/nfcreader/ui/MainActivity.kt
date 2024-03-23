package com.rbs.nfcreader.ui

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.rbs.nfcreader.R
import com.rbs.nfcreader.data.model.Card
import com.rbs.nfcreader.databinding.ActivityMainBinding
import com.rbs.nfcreader.utils.ViewModelFactory

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private lateinit var nfcAdapter: NfcAdapter
    private lateinit var pendingIntent: PendingIntent
    private lateinit var intentFilter: Array<IntentFilter>
    private lateinit var cardAdapter: CardAdapter

    private val viewModel: CardViewModel by viewModels {
        ViewModelFactory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setNFC()
        setAdapter()
        setData()

        with(binding) {
            btnSave.setOnClickListener {
                val serialNumber = tvSerialNumber.text.toString()
                val messages = tvMessages.text.toString()

                if (serialNumber.isNotEmpty() && messages.isNotEmpty()) {
                    val data = Card(serialNumber = serialNumber, message = messages)
                    viewModel.insert(data)
                    refresh()
                } else {
                    setToast(getString(R.string.text_no_data_add))
                }
            }
        }
    }

    private fun setNFC() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (!nfcAdapter.isEnabled) setToast(getString(R.string.text_enable_nfc))

        pendingIntent = PendingIntent.getActivity(
            this, 0, Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent.FLAG_MUTABLE
        )

        intentFilter = arrayOf(
            IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED),
            IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED),
            IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED)
        )
    }

    private fun setAdapter() {
        cardAdapter = CardAdapter()
        with(binding.rvCard) {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = cardAdapter
        }
    }

    private fun setData() {
        with(viewModel) {
            getAllData().observe(this@MainActivity) {
                with(cardAdapter) {
                    submitList(it)
                    setOnItemClickCallback(object : CardAdapter.OnItemClickCallback {
                        override fun onDeleteItem(id: Int) {
                            delete(id)
                            refresh()
                        }

                        override fun onSendItem(serialNumber: String, message: String) {
                            send(serialNumber, message)
                            setToast(getString(R.string.text_successfully_send_data))
                        }
                    })
                }
            }
        }
    }

    private fun refresh() {
        finish()
        startActivity(intent)
    }

    private fun setToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        val techList = arrayOf<Array<String>>()
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilter, techList)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        nfcAdapter.disableForegroundDispatch(this)
    }

    @Suppress("DEPRECATION")
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        val action = intent.action
        if (action == NfcAdapter.ACTION_TAG_DISCOVERED || action == NfcAdapter.ACTION_TECH_DISCOVERED || action == NfcAdapter.ACTION_NDEF_DISCOVERED) {
            with(intent) {
                val id = getByteArrayExtra(NfcAdapter.EXTRA_ID)

                with(binding) {
                    val value =
                        id?.joinToString(separator = ":") { eachByte -> "%02X".format(eachByte) }
                    tvSerialNumber.text = value

                    val messages = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        getParcelableArrayExtra(
                            NfcAdapter.EXTRA_NDEF_MESSAGES,
                            NdefMessage::class.java
                        )
                    } else {
                        getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
                    }

                    val ndefMessages = mutableListOf<NdefMessage>()
                    if (messages != null) {
                        for (i in messages.indices) {
                            ndefMessages.add(i, messages[i] as NdefMessage)
                        }
                        val record = ndefMessages[0].records[0]
                        val payload = record.payload
                        val values = payload.toString()
                        tvMessages.text = values
                    } else {
                        tvMessages.text = getString(R.string.text_cant_read_message)
                    }
                }
            }
        }
    }
}