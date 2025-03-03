package com.appjava.springboot.app.controllers;

import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.appjava.springboot.app.models.entity.Cliente;
import com.appjava.springboot.app.models.service.IClienteService;
import com.appjava.springboot.app.util.paginator.PageRender;

@Controller
@SessionAttributes("cliente")
public class ClienteController {

	@Autowired
	private IClienteService clienteService;

	@RequestMapping(value = {"/index","/"}, method = RequestMethod.GET)
	public String listar(@RequestParam(name="page",defaultValue = "0") int page, Model model) {
		
		Pageable pageRequest = PageRequest.of(page, 5);
		
		Page<Cliente> clientes = clienteService.findAll(pageRequest);
		
		PageRender<Cliente> pageRender = new PageRender<>("/listar", clientes);
		model.addAttribute("titulo", "Listado de Clientes");
		model.addAttribute("clientes", clientes);
		model.addAttribute("page", pageRender);
		return "index";
	}

	@GetMapping("/form")
	public String crear(Map<String, Object> model) {

		Cliente cliente = new Cliente();

		model.put("cliente", cliente);
		model.put("titulo", "Formulario de Cliente");

		return "form";
	}

	@GetMapping("/form/{id}")
	public String editar(@PathVariable(value = "id") Long id, Map<String, Object> model, RedirectAttributes flash) {

		Cliente cliente = null;

		if (id > 0) {
			cliente = clienteService.findOne(id);
			if (cliente == null) {
				flash.addFlashAttribute("error", "¡El ID del cliente no existe!");
				return "redirect:/index";
			}
		} else {
			flash.addFlashAttribute("error", "¡El ID del cliente no puede ser cero!");
			return "redirect:/index";
		}
		model.put("cliente", cliente);
		model.put("titulo", "Editar Cliente");
		return "form";
	}

	@PostMapping("/form")
	public String guardar(@Valid Cliente cliente, BindingResult result, Model model, RedirectAttributes flash,
			SessionStatus status) {

		if (result.hasErrors()) {
			model.addAttribute("titulo", "Formulario de Cliente");
			return "form";
		}
		String mensajeFlash = (cliente.getId() != null)? "¡Cliente editado con éxito!" : "¡Cliente creado con éxito!";

		clienteService.save(cliente); // le pasamos el metodo de guardar
		status.setComplete(); // elimina el objeto cliente de la sesión
		flash.addFlashAttribute("success", mensajeFlash);
		return "redirect:index"; // redirige al listado de cliente

	}

	@GetMapping("/eliminar/{id}")
	public String eliminar(@PathVariable(value = "id") Long id, RedirectAttributes flash) {

		if (id > 0) {
			clienteService.delete(id);
			flash.addFlashAttribute("success", "¡Cliente eliminado con éxito!");
		}
		return "redirect:/index";
	}

}
