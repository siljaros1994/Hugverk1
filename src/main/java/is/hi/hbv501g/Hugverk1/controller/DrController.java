package is.hi.hbv501g.Hugverk1.controller;

import is.hi.hbv501g.Hugverk1.Persistence.Entities.MyAppUsers;
import is.hi.hbv501g.Hugverk1.Persistence.Repositories.MyAppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

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
    public String displayUsers(Model model,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<MyAppUsers> usersPage = myAppUserRepository.findAll(pageable);
        model.addAttribute("users", usersPage.getContent());
        model.addAttribute("totalPages", usersPage.getTotalPages());
        model.addAttribute("currentPage", page);
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
