package com.example.gamelb.fragments

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gamelb.R
import com.example.gamelb.adapters.GameEntityAdapter
import com.example.gamelb.checkDate
import com.example.gamelb.db.*

class MyCollectionFragment : Fragment(R.layout.fragment_my_list) {


    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var progressBar: ProgressBar

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val view: View? = getView()
        progressBar = view?.findViewById(R.id.progress)!!
        val db : AppDatabase = AppDatabase.getDatabase(requireActivity().application)
        val repository = GameEntityRepository(db.gameEntityDAO())
        val gameEntityViewModel = GameEntityViewModel(repository)
        val myAdapter = GameEntityAdapter()
        val recyclerview: RecyclerView = view.findViewById(R.id.recyclerview_explore_games)!!
        recyclerview.adapter = myAdapter
        linearLayoutManager = LinearLayoutManager(context)
        recyclerview.layoutManager = linearLayoutManager

        val gamesObserver = Observer<List<GameEntity>> { games ->
            // filter based on whether the game is already released or not
            val filteredGames = games.filter {
                !checkDate(it.released)
            }
            myAdapter.setDatasource(filteredGames)
            myAdapter.notifyDataSetChanged()
            progressBar.visibility = View.GONE
        }

        // observe the liveData
        gameEntityViewModel.games.observe(viewLifecycleOwner, gamesObserver)

    }
}