package com.example.secondhomework.presentation

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.secondhomework.databinding.FragmentCountdowntimerBinding
import com.example.secondhomework.presentation.TimerViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.time.Duration


class CountDownTimerFragment : Fragment() {

    private val viewModel by lazy{
        ViewModelProvider(this)[TimerViewModel::class.java]
    }
    private var _binding: FragmentCountdowntimerBinding? = null
    private val binding get() = _binding!!
    private val compositeDisposable= CompositeDisposable()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCountdowntimerBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
        _binding = null
    }

    @SuppressLint("CheckResult")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                viewModel.timer.maxValue = progress
                binding.startButton.isEnabled=true
                if(binding.minutesMode.isChecked){
                    binding.showProgress.text= convertToFormat(progress)
                }else{
                    binding.showProgress.text=progress.toString()
                }

            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        binding.startButton.setOnClickListener {
            viewModel.start()
                .observeOn(AndroidSchedulers.mainThread())
                .map{
                    if (binding.minutesMode.isChecked){
                       convertToFormat(it)
                    }else{
                        it.toString()
                    }
                }
                .subscribe {
                    binding.showProgress.text=it
                    binding.startButton.isEnabled=false
                }.also{compositeDisposable.add(it)}


        }

        binding.pauseButton.setOnClickListener {
            viewModel.pause()
            binding.startButton.isEnabled=true

        }

        binding.stopButton.setOnClickListener {
            viewModel.stop()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    binding.seekbar.progress=it
                }.also{compositeDisposable.add(it)}
            binding.startButton.isEnabled=true
        }

        binding.minutesMode.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                Toast.makeText(activity,"Конвертация в формат 00:00", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(activity,"Конвертация в секунды ", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //Перевод в формат mm:ss
    @RequiresApi(Build.VERSION_CODES.O)
    private fun convertToFormat(seconds:Int):String {
        val formatter = DateTimeFormatter.ofPattern("mm:ss")
        val currentTime=LocalTime.ofSecondOfDay(seconds.toLong())
        return formatter.format(currentTime)
    }


}