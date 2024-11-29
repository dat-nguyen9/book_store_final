package com.book_store.controller.admin_controller;

import com.book_store.entity.Customer;
import com.book_store.entity.User;
import com.book_store.repository.RoleRepository;
import com.book_store.repository.UserRepository;
import com.book_store.service.CustomerService;
import com.book_store.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;
import java.util.List;

@RequestMapping("/admin")
@Controller
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/customer")
    public String viewCustomer(Model model,
                               @RequestParam(value = "keyword", defaultValue = "") String keyword,
                               @RequestParam(value = "sortField", defaultValue = "id") String sortField,
                               @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir) {
        return listByPagesCustomer(1, sortField, sortDir, keyword, model);
    }

    @GetMapping("/customer/{pageNumber}")
    public String listByPagesCustomer(@PathVariable(name = "pageNumber") int currentPage,
                                      @RequestParam(value = "sortField", defaultValue = "id") String sortField,
                                      @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir,
                                      @RequestParam(value = "keyword", defaultValue = "") String keyword,
                                      Model model) {
        Page<Customer> page = customerService.listAllCustomer(currentPage, sortField, sortDir, keyword);
        long totalItems = page.getTotalElements();
        int totalPages = page.getTotalPages();
        List<Customer> listcustomer = page.getContent();
        model.addAttribute("listcustomer", listcustomer);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("keyword", keyword);
        model.addAttribute("query", "?sortField=" + sortField + "&sortDir="
                + sortDir + "&keyword=" + keyword);
        return "admin_customer";
    }

    //function edit customer by id
    @RequestMapping("/customer/edit/{id}")
    public ModelAndView showEditCustomerPage(@PathVariable(name = "id") int id) {
        ModelAndView mav = new ModelAndView("admin_customer_edit");
        User user = userService.getById(id);
        mav.addObject("user", user);
        return mav;
    }

    //function save customerEdit
    @RequestMapping(value = "/customer/saveEdit", method = RequestMethod.POST)
    public String saveCustomerEdit(@ModelAttribute("user") User user) {
        //user
        User userSave = userService.getById(user.getId());
        userSave.setUsername(user.getUsername());
        //customer
        Customer customerSave = customerService.getById(user.getCustomer().getId());
        customerSave.setStatus(user.getCustomer().getStatus());
        customerSave.setName(user.getCustomer().getName());
        customerSave.setEmail(user.getCustomer().getEmail());
        customerSave.setPhone(user.getCustomer().getPhone());
        customerSave.setAddress(user.getCustomer().getAddress());
        customerSave.setBirthday(user.getCustomer().getBirthday());
        customerSave.setAvatar(user.getCustomer().getAvatar());
        customerService.saveCustomer(customerSave);
        userService.saveUser(userSave);
        return "redirect:/admin/customer";
    }

    //function delete customer by id
    @RequestMapping("/customer/disable/{id}")
    public String disableCustomer(@PathVariable(name = "id") int id) {
        User user = userService.getById(id);
        user.getCustomer().setStatus(0);
        userService.saveUser(user);
        return "redirect:/admin/customer";
    }

    //function create new customer
    @RequestMapping("/customer/new")
    public String showNewCustomerPage(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "admin_customer_add";
    }

    //function save customer
    @RequestMapping(value = "/customer/save", method = RequestMethod.POST)
    public RedirectView saveCustomerAdmin(@Valid User user, RedirectAttributes model) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(user.getPassword());
        user.getCustomer().setStatus(1);
        user.setPassword(encodedPassword);
        user.setUsername(user.getUsername());
        user.setRole(roleRepository.getById(5));
        Customer customer = user.getCustomer();
        customer.setName(user.getCustomer().getName());
        customer.setEmail(user.getCustomer().getEmail());
        customer.setPhone(user.getCustomer().getPhone());
        customer.setAddress(user.getCustomer().getAddress());
        customer.setBirthday(user.getCustomer().getBirthday());
        customer.setAvatar(user.getCustomer().getAvatar());
        customer.setPoint(0);
        if (userRepository.findByUsername(user.getUsername()) != null) {
            model.addFlashAttribute("alert_Username", "Tên đăng nhập đã được dùng!");
            return new RedirectView("/admin/customer/new");
        } else if (customerService.findByEmail(user.getCustomer().getEmail()) != null) {
            model.addFlashAttribute("alert_Email", "Email đã được dùng!");
            return new RedirectView("/admin/customer/new");
        }
        customerService.saveCustomer(customer);
        user.setCustomer(customer);
        userService.saveUser(user);
        return new RedirectView("/admin/customer");
    }
}
