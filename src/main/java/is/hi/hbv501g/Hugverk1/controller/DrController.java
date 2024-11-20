package is.hi.hbv501g.Hugverk1.controller;

import is.hi.hbv501g.Hugverk1.Persistence.Entities.MyAppUsers;
import is.hi.hbv501g.Hugverk1.Persistence.Repositories.MyAppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
//@RequestMapping("/dr/")
public class DrController {

    @Autowired
    private MyAppUserRepository myAppUserRepository;

    @Autowired
    public DrController(MyAppUserRepository myAppUserRepository){
        this.myAppUserRepository = myAppUserRepository;
    }

    @GetMapping("/dr")
    public String displayUsers(Model model) {
        List<MyAppUsers> users = myAppUserRepository.findAll();
        model.addAttribute("users", users);
        return "dr";
    }


    @GetMapping("/delete/{username}")
    public String deleteUser(@PathVariable("username") String username, Model model){
        System.out.println("Delete request received for username: " + username);
        MyAppUsers user = myAppUserRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Invalid user:" + username));
        myAppUserRepository.delete(user);
        model.addAttribute("users", myAppUserRepository.findAll());
        return "dr";
    }
}
