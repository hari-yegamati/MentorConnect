package com.example.mentormatch.controller;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.mentormatch.entity.Currentuser;
import com.example.mentormatch.entity.Prof;
import com.example.mentormatch.entity.ProjectForm;
import com.example.mentormatch.entity.Projects;
import com.example.mentormatch.entity.User;
import com.example.mentormatch.repository.CurrentuserRepository;
import com.example.mentormatch.repository.ProfRepository;
import com.example.mentormatch.repository.ProjectsRepository;
import com.example.mentormatch.service.EmailService;
import com.example.mentormatch.service.LoginService;
import com.example.mentormatch.service.ZoomService;

import jakarta.servlet.http.HttpSession;


@Controller
public class MentormatchController {

    @Autowired
    LoginService service;
    
    @Autowired
    private CurrentuserRepository currrepo;

    @GetMapping({"/","/login"})
    public String login(RedirectAttributes redirectAttributes) {
        Iterable<Currentuser> curuser = currrepo.findAll();
        for(Currentuser x:curuser){
            User user=service.find(x.getUsername());
            redirectAttributes.addAttribute("user",user);
            return "redirect:/home";
        }
        return "sign_in";
    }
    @GetMapping("/logout")
    public String logout(){
        currrepo.deleteAll();
        return "redirect:/";
    }

    @GetMapping("/sign_in")
    public String signin(@ModelAttribute("case")String msg,RedirectAttributes redirectAttributes,Model model){
        model.addAttribute("msg",msg);
        return "sign_in";
    }

    @GetMapping("/signup")
    public String signup() {
        return "sign_up";
    }

    @PostMapping("/signup")
    public String signup(User user) {
        if(service.SaveOrUpdate(user)){
            return "sign_in";
        }
            return "sign_up";
    }

    @PostMapping("/login")
    public String loginSubmit(@RequestParam String username, @RequestParam String password,RedirectAttributes redirectAttributes ,Model model) {
        User user = service.find(username);
        if(user!=null){
            if(password.equals(user.getPassword())){
                Currentuser newcurr=new Currentuser();
                newcurr.setUsername(user.getUsername());
                newcurr.setPassword(user.getPassword());
                newcurr.setEmail(user.getEmail());
                currrepo.save(newcurr);
                redirectAttributes.addAttribute("user", user);
                return "redirect:/home";
            }
        }
        redirectAttributes.addFlashAttribute("case","invalid login credentials");
        return "redirect:/sign_in";
    }

    @GetMapping("/home")
    public String home(@ModelAttribute("user") User user,Model model,RedirectAttributes redirectAttributes){
        if(user.getUsername()==null){
           Iterable<Currentuser> a=currrepo.findAll();
           for(Currentuser x: a){
            User use=service.find(x.getUsername());
            model.addAttribute("user", use);
            break;
           }
           return "home";
        }
        else{
            model.addAttribute("user", user);
            return "home";
        }
    }

    @GetMapping("/newproj")
    public String newproj(){
        return "newproject";
    }
    @PostMapping("/newproj")
    public String newprojsave(@RequestParam String name, @RequestParam String department,@RequestParam String lang,Model model){
        List<Prof> professors = profRepository.findAll();
        List<Prof> updatedprofessors=professors.stream().filter(professor->professor.getSkills().contains(lang)).collect(Collectors.toList());
        model.addAttribute("profs",updatedprofessors);
        return "reqprofessor";
    }

    @Autowired
    private ProfRepository profRepository;

    @GetMapping("/addprof")
    public String showAddProfForm(Model model) {
        model.addAttribute("prof", new Prof());
        return "x";
    }

    @PostMapping("/addprof")
    public String saveProf(@ModelAttribute Prof prof, @RequestParam("file") MultipartFile file) {
        prof.setImage(file.getOriginalFilename());
        String un = prof.getUsername();
        prof.setImage(un+".jpg");
        Prof ne=profRepository.save(prof);
        if(ne!=null){
            try {
                String directoryPath = "src/main/resources/static/Images";
                Path directory = Paths.get(directoryPath);
                Files.createDirectories(directory);
                Path filePath = directory.resolve(un+".jpg");
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                return "redirect:/profs";
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "redirect:/addprof";
    }

    @GetMapping("/profs")
    public String getAllProfessors(Model model) {
        Iterable<Prof> professors = profRepository.findAll();
        model.addAttribute("profs", professors);
        return "professors_list"; 
    }

    //newproject
    @GetMapping("/new-project")
    public String showNewProjectForm(Model model) {
        model.addAttribute("projectForm", new ProjectForm());
        return "newproject";
    }

    
    @PostMapping("/project")
    public String saveProject(@ModelAttribute("projectForm") ProjectForm projectForm, HttpSession session,Model model) {
        session.setAttribute("projectForm", projectForm);
        List<Prof> professors = profRepository.findAll();
        List<Prof> updatedprofessors=professors.stream().filter(professor->professor.getSkills().contains(projectForm.getLanguage())).collect(Collectors.toList());
        model.addAttribute("profs", updatedprofessors);
        return "reqprofessor";
    }

    @Autowired
    private ProjectsRepository projrepo;

    @Autowired
    private EmailService mailservice;


    private final ZoomService zoomService;

    public MentormatchController(ZoomService zoomService) {
        this.zoomService = zoomService;
    }


    @PostMapping("/select-professor/{professorId}")
    public String selectProfessor(@PathVariable Long professorId,HttpSession session,Model model) {
                                    
        ProjectForm projectForm = (ProjectForm) session.getAttribute("projectForm");
        if (projectForm == null) {
            return "redirect:/new-project";
        }
        Optional<Prof> optionalpr =profRepository.findById(professorId);
        Prof prf=optionalpr.orElse(null);
        Currentuser cuser=new Currentuser();
        Iterable<Currentuser> x=currrepo.findAll();
        for (Currentuser curuser:x){
            cuser=curuser;
            break;
        }


        //scheduling the meeting
        String accessToken = zoomService.getAccessToken();
        System.out.println(accessToken);
        // String accessToken = zoomService.getAccessToken();
        if (accessToken != null) {
            Map<String, String> result = new HashMap<>();
            result= zoomService.scheduleMeeting(accessToken);
            if(result!=null){
            model.addAttribute("result",result);
            Projects neww=new Projects();
            neww.setProjectName(projectForm.getProjectName());
            neww.setStudentName(cuser.getUsername());
            neww.setDepartment(projectForm.getDepartment());
            neww.setProfessorName(prf.getUsername());
            neww.setLanguage(projectForm.getLanguage());
            neww.setStudentEmail(cuser.getEmail());
            neww.setProfessorEmail(prf.getEmail());
            Projects pr=projrepo.save(neww);
            if(pr!=null){
                String stdSub = "Meeting Confirmation with " + prf.getUsername();
                String stdbody = "Dear " + cuser.getUsername() + ",\n\n"
                    + "Congratulations! Your meeting request is accepted by " + prf.getUsername() + " for your project discussion.\n\n"
                    +"\nMeeting Details :\n\n"+"link : "+result.get("join_url")+"\n\nmeeting id: "+result.get("meetingid")+"\npassword: "+result.get("password")
                    + "Here are the details of your project:\n"
                    + "- Project Name: " + projectForm.getProjectName() + "\n"
                    + "- Department: " + projectForm.getDepartment() + "\n"
                    + "- Language: " + projectForm.getLanguage() + "\n\n"
                    + "Please feel free to reach out to " + prf.getUsername() + " at " + prf.getEmail() + " to discuss further details and plan your project.\n\n"
                    + "Best Regards,\n"
                    + "Mentor Connect";

                mailservice.sendEmail(cuser.getEmail(), stdSub, stdbody);

                String profSub = "Meeting request from " + cuser.getUsername();
                String profbody = "Dear " + prf.getUsername() + ",\n\n"
                    + "You have been selected by " + cuser.getUsername() + " for a project discussion.\n\n"
                    +"\nMeeting Details :\n\n"+"- link : "+result.get("join_url")+"\n\n- meeting id: "+result.get("meetingid")+"\n- password: "+result.get("password")+" \n\nHOSTKEY = 731238\n\n"
                    + "Here are the project details:\n"
                    + "- Project Name: " + projectForm.getProjectName()  + "\n"
                    + "- Department: " + projectForm.getDepartment() + "\n"
                    + "- Language: " + projectForm.getLanguage() + "\n"
                    + "- Student Name: " + cuser.getUsername() + "\n"
                    + "- Student Email: " + cuser.getEmail() + "\n\n"
                    + "Please contact " + cuser.getUsername() + " at " + cuser.getEmail() + " to discuss the project further and coordinate the collaboration.\n\n"
                    + "Best Regards,\n"
                    + "MentorConnect";

                mailservice.sendEmail(prf.getEmail(), profSub, profbody);
                model.addAttribute("id", result.get("meetingid"));
                model.addAttribute("password", result.get("password"));
                model.addAttribute("link", result.get("join_url"));
                return "success";
            }
        } 
        else {
            model.addAttribute("out", "error occured");
            return "hello";
        }
        }
        session.removeAttribute("projectForm");
        return "hello";
    }


    //my-projects
    @GetMapping("/my-projects")
    public String myprojects(Model model){
        Currentuser cuser=new Currentuser();
        Iterable<Currentuser> x=currrepo.findAll();
        for (Currentuser curuser:x){
            cuser=curuser;
            break;
        }
        String usermail=cuser.getEmail();
        List<Projects> pr=projrepo.findAll();
        List<Projects> upr=pr.stream().filter(p->p.getStudentEmail().equals(usermail)).collect(Collectors.toList());
        model.addAttribute("projs", upr);
        return "myprojects";
    }


    
    // @GetMapping("/schedule-zoom-meeting")
    // public String scheduleZoomMeeting(Model model,HttpSession session) {
    //     System.out.println("mainn");
    //     String accessToken = zoomService.getAccessToken();
    //     System.out.println(accessToken);
    //     // String accessToken = zoomService.getAccessToken();
    //     if (accessToken != null) {
    //         String out= zoomService.scheduleMeeting(accessToken);
    //         model.addAttribute("out",out);
    //         return "hello";
    //     } else {
    //         model.addAttribute("out", "error occured");
    //         return "hello";
    //     }
    // }
}
//        List<Prof> updatedprofessors=professors.stream().filter(professor->professor.getSkills().contains(projectForm.getLanguage())).collect(Collectors.toList());
