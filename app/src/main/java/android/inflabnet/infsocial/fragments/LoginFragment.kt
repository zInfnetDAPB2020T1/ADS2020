package android.inflabnet.infsocial.fragments

import android.content.Intent
import android.inflabnet.infsocial.HomeActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.inflabnet.infsocial.R
import android.inflabnet.infsocial.model.User
import android.inflabnet.infsocial.viewmodel.UserViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_login.*

/**
 * A simple [Fragment] subclass.
 */
class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        var userViewModel: UserViewModel? = null
        activity?.let{
            userViewModel = ViewModelProviders.of(it)[UserViewModel::class.java]
        }

        var userModel = userViewModel!!.user?.nome
        var passModel = userViewModel!!.user?.pass
        if ( userModel != null && !userModel.isNullOrBlank()) {
            //Toast.makeText(this.context!!.applicationContext, "$user", Toast.LENGTH_SHORT ).show()
            //if ( user != null) {
            txtNome.setText("$userModel")
        }

        btnCadastro.setOnClickListener {
            var nome = txtNome.text.toString()
            var pass = txtPass.text.toString()
            userViewModel!!.user = User(nome,pass)
            findNavController().navigate(R.id.action_loginFragment_to_cadastroFragment)
        }
        btnNext.setOnClickListener{
            //            Toast.makeText(this.context!!.applicationContext, "$userModel and $passModel - ${txtNome.text} and ${txtPass.text}", Toast.LENGTH_LONG ).show()
//            if(
//                txtNome.text.toString().equals(userModel) &&
//                txtPass.text.toString().equals(passModel)){
//                    var intt = Intent(this.context!!.applicationContext,HomeActivity::class.java)
//                    startActivity(intt)
//                }
//            else(
//                    Toast.makeText(this.context!!.applicationContext, "Usuário Inválido", Toast.LENGTH_LONG)
//                    )
            var intt = Intent(this.context!!.applicationContext, HomeActivity::class.java)
            startActivity(intt)
        }
    }

}
