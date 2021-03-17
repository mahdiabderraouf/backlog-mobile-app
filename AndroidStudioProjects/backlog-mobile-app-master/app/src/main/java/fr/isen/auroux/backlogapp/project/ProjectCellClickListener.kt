package fr.isen.auroux.backlogapp.project

import fr.isen.auroux.backlogapp.network.Project

interface ProjectCellClickListener {
    fun onClickListener(project: Project)
}