package com.appleobject.mynotes.ui

import android.app.AlertDialog
import android.os.AsyncTask
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.navigation.Navigation

import com.appleobject.mynotes.R
import com.appleobject.mynotes.db.Note
import com.appleobject.mynotes.db.NoteDatabase
import kotlinx.android.synthetic.main.fragment_add_note.*
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 */
class AddNoteFragment : BaseFragment() {
    var note :Note? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Because we want to create a menu
        setHasOptionsMenu(true)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_note, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        arguments?.let {
            note = AddNoteFragmentArgs.fromBundle(it).note
            et_title.setText(note?.title)
            et_note.setText(note?.note)
        }

        // Onclick of the floating action bar
        btn_save.setOnClickListener {
            val noteTitle = et_title.text.toString().trim()
            val noteBody = et_note.text.toString().trim()

            if (noteTitle.isEmpty())
            {
                et_title.error = "Title Required..."
                et_title.requestFocus()
                return@setOnClickListener
            }

            if (noteBody.isEmpty())
            {

                et_note.error = "Note Required..."
                et_note.requestFocus()
                return@setOnClickListener
            }

            launch {
                context?.let {
                    val mNote = Note(noteTitle,noteBody)
                    if (note == null){
                        NoteDatabase(it).getNoteDao().addNote(mNote)
                        it.toast("Note Saved...")
                    }else{
                        mNote.id = note!!.id
                        NoteDatabase(it).getNoteDao().updateNote(mNote)
                        it.toast("Note Updated...")
                    }
                    val action = AddNoteFragmentDirections.linkHome()
                    view?.let { it1 -> Navigation.findNavController(it1).navigate(action) }
                }

            }


        }
    }

    // For the delete option on the menu options
   private fun deleteNote(){
        AlertDialog.Builder(context).apply {
            setTitle("Are you sure?")
            setMessage("You cannot undo this operation!!!")
            setPositiveButton("Yes"){_,_->
                launch {
                    NoteDatabase(context).getNoteDao().deleteNote(note!!)
                    val action = AddNoteFragmentDirections.linkHome()
                    view?.let { Navigation.findNavController(it).navigate(action) }
                }
            }

            setNegativeButton("No"){_,_ ->

            }
        }.create().show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.delete -> if (note != null ) deleteNote() else context?.toast("Cannot Delete...")
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu,menu)
    }


}
