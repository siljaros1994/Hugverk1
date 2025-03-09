package is.hi.hbv501g.Hugverk1.controller;

import is.hi.hbv501g.Hugverk1.Persistence.Entities.MyAppUsers;
import is.hi.hbv501g.Hugverk1.Persistence.Repositories.MyAppUserRepository;
import is.hi.hbv501g.Hugverk1.dto.DeleteResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

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

    @DeleteMapping("/delete/{username}")
    public ResponseEntity<DeleteResponseDTO> deleteUser(@PathVariable("username") String username) {
        Optional<MyAppUsers> user = myAppUserRepository.findByUsername(username);
        if (user.isPresent()) {
            myAppUserRepository.delete(user.get());
            return ResponseEntity.ok(new DeleteResponseDTO("User deleted successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new DeleteResponseDTO("User not found"));
        }
    }


}
