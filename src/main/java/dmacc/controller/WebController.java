package dmacc.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import dmacc.beans.Contact;
import dmacc.repository.ContactRepository;

@Controller
public class WebController {
	@Autowired
	ContactRepository repo;

	/*
	 * When the viewAll link is called, the program will run this method and return
	 * all the contacts as an attribute. Finally, it will launch to the results.html
	 * page.
	 */
	@GetMapping("/viewAll")
	public String viewAllContacts(Model model) {
		model.addAttribute("contacts", repo.findAll());
		return "results";
	}

	/*
	 * The GET request provides us with a new default no arg constructor contact to
	 * pass in for data to the form.
	 */
	@GetMapping("/inputContact")
	public String addNewContact(Model model) {
		Contact c = new Contact();
		model.addAttribute("newContact", c);
		return "input";
	}

	/*
	 * The second method is a post mapping – this will happen after the form is
	 * submitted. We will save the Contact that was passed in, query for updated
	 * items in the repo and launch back into our results list. The application will
	 * control which one is called. If it’s a GET mapping, the first method is
	 * called. If it a form submit, the second method is called. How amazingly easy
	 * are these annotations? Saves us a lot of code!
	 */
	@PostMapping("/inputContact")
	public String addNewContact(@ModelAttribute Contact c, Model model) {
		repo.save(c);
		model.addAttribute("contacts", repo.findAll());
		return "results";
	}

	/*
	 * We are going to try to find an element by the ID provided. This is a
	 * different way to write. Essentially it states to find the Contact by using
	 * the method. But If it can’t, then throw this IllegalArgument Exception. This
	 * is a lambda function. We won’t get into them in our course, but I wanted you
	 * to see one. I’d recommend checking it out during your downtime to become more
	 * familiar with the concept.
	 */
	@GetMapping("/edit/{id}")
	public String showUpdateForm(@PathVariable("id") long id, Model model) {
		Contact c = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
		model.addAttribute("contact", c);
		return "update";
	}

	@PostMapping("/update/{id}")
	public String updateUser(@PathVariable("id") long id, @Valid Contact c, BindingResult result, Model model) {
		if (result.hasErrors()) {
			c.setId(id);
			return "update";
		}
		repo.save(c);
		model.addAttribute("contacts", repo.findAll());
		return "results";
	}

	/*
	 * This is actually easier to implement because we don’t need to pass the
	 * Contact over to a form. Instead, when delete is called, it grabs the id,
	 * removes it from the database and redirects it back to the results page.
	 */
	@GetMapping("/delete/{id}")
	public String deleteUser(@PathVariable("id") long id, Model model) {
		Contact c = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
		repo.delete(c);
		model.addAttribute("contacts", repo.findAll());
		return "results";
	}
}
