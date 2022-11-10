package com.example.workmangerstudy

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.workmangerstudy.databinding.FragmentFirstBinding
import java.util.*
import java.util.concurrent.TimeUnit

class FirstFragment : Fragment() {
    //https://medium.com/@anteprocess/how-to-use-android-jetpacks-workmanager-840fef258985
    //https://medium.com/androiddevelopers/workmanager-periodicity-ff35185ff006
    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonFirst.setOnClickListener {
//            setTimeRequest()
//            setOneTimeRequest()
            setOneTimeRequestWithData()
//            setPeriodicRequest()
//            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
    }

    private fun setTimeRequest() {
        val currentDate = Calendar.getInstance()
        val dueDate = Calendar.getInstance()// Set Execution around 05:00:00 AM
        dueDate.set(Calendar.HOUR_OF_DAY, 9)
        dueDate.set(Calendar.MINUTE, 24)
        dueDate.set(Calendar.SECOND, 0)
        if (dueDate.before(currentDate)) {
            dueDate.add(Calendar.HOUR_OF_DAY, 24)
        }
        val timeDiff = dueDate.timeInMillis - currentDate.timeInMillis
        val dailyWorkRequest = OneTimeWorkRequest.Builder(DailyWorker::class.java).setInitialDelay(timeDiff, TimeUnit.MILLISECONDS).build()
        WorkManager.getInstance(requireContext()).enqueue(dailyWorkRequest)
    }

    private fun setOneTimeRequest() {
        val notificationRequest: OneTimeWorkRequest = OneTimeWorkRequest.Builder(NotificationWorkManager::class.java).build()
        val workManager = WorkManager.getInstance(requireContext())
        workManager.enqueue(notificationRequest)
        workManager.getWorkInfoByIdLiveData(notificationRequest.id).observe(requireActivity()) {
            Log.i("mLog", it.state.name)
        }
    }

    private fun setOneTimeRequestWithData() {
        val data: Data = Data.Builder().putString("mKey", "Hello").build()
        val notificationRequest: OneTimeWorkRequest = OneTimeWorkRequest.Builder(NotificationWorkManagerWithData::class.java).setInputData(data).build()
        val workManager = WorkManager.getInstance(requireContext())
        workManager.enqueue(notificationRequest)
        workManager.getWorkInfoByIdLiveData(notificationRequest.id).observe(requireActivity()) {
            Log.i("mLog", it.state.name)
            if (it.state.isFinished) {
                val data = it.outputData
                val message = data.getString("mKey")
                Log.i("mLog", "$message")

            }

        }
    }

    private fun setPeriodicRequest() {
        val notificationRequest: PeriodicWorkRequest = PeriodicWorkRequest.Builder(NotificationWorkManager::class.java, 15, TimeUnit.MINUTES).build()
        WorkManager.getInstance(requireContext()).enqueue(notificationRequest)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}