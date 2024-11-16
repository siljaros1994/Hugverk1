package is.hi.hbv501g.Hugverk1.controller;

import is.hi.hbv501g.Hugverk1.Persistence.Entities.MyAppUsers;
import is.hi.hbv501g.Hugverk1.Persistence.Repositories.MyAppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
//@RequestMapping("/dr/")
public class AdminReportsController {

    @Autowired
    private MyAppUserRepository myAppUserRepository;

    @Autowired
    public AdminReportsController(MyAppUserRepository myAppUserRepository){
        this.myAppUserRepository = myAppUserRepository;
    }

    @GetMapping("/reports")
    public String adminReports(Model model) {
        List<MyAppUsers> users = myAppUserRepository.findAll();
        model.addAttribute("users", users);
        return "adminReports";
    }



}
