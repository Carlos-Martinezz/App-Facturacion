package com.carlos.springboot.app.controllers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.carlos.springboot.app.models.entity.Cliente;
import com.carlos.springboot.app.models.service.IClienteService;
import com.carlos.springboot.app.models.service.IUploadFilesService;
import com.carlos.springboot.app.util.paginator.PageRender;
import com.carlos.springboot.app.view.xml.ClienteList;

@Controller
@SessionAttributes("cliente")
public class ClienteController {

	protected final Log logger = LogFactory.getLog(this.getClass());

	@Autowired
	private IClienteService clienteService;

	@Autowired
	private IUploadFilesService uploadFileService;
	
	@Autowired
	private MessageSource messageSource;

	@GetMapping(value = "/listar-rest")
	public @ResponseBody ClienteList listarRest() { 
		
		return new ClienteList(clienteService.findAll());
	}
	
	@RequestMapping(value = {"/listar", "/"}, method = RequestMethod.GET)
	public String listar(@RequestParam(name = "page", 
			defaultValue = "0") int page, 
			Model model, 
			Authentication authentication, 
			HttpServletRequest request,
			Locale locale) {
		
		if(authentication != null) {
			logger.info("Hola usuario autenticado, tu username es: ".concat(authentication.getName()));
		}
		
		//Utilizando Authentication de forma estática
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if(auth != null) {
			logger.info("Auth Static Hola  usuario autenticado, tu username es: ".concat(auth.getName()));
		}
		
		if(hasRole("ROLE_ADMIN")) {
			logger.info("Hola ".concat(auth.getName()).concat(". Tienes acceso"));
		} else {
			logger.info("Hola ".concat(auth.getName()).concat(". No tienes acceso"));
		}
		
		//Otra forma de tener el rol
		SecurityContextHolderAwareRequestWrapper securityContext = new SecurityContextHolderAwareRequestWrapper(request, "");
		
		if(securityContext.isUserInRole("ROLE_ADMIN")) {
			logger.info("Forma usando SecurityContextHolderAwareRequestWrapper Hola ".concat(auth.getName()).concat(". Tienes acceso"));
		} else {
			logger.info("Forma usando SecurityContextHolderAwareRequestWrapper Hola ".concat(auth.getName()).concat(". No tienes acceso"));
		}
		
		//Validando con el request
		if(request.isUserInRole("ROLE_ADMIN")) {
			logger.info("Forma usando HttpServletRequest Hola ".concat(auth.getName()).concat(". Tienes acceso"));
		} else {
			logger.info("Forma usando HttpServletRequest Hola ".concat(auth.getName()).concat(". No tienes acceso"));
		}
		
		//Implementando paginación Back End
		Pageable pageRequest = PageRequest.of(page, 4);
		Page<Cliente> clientes = clienteService.findAll(pageRequest);
		
		PageRender<Cliente> pageRender = new PageRender<>("/listar", clientes);
		
		
		model.addAttribute("titulo", messageSource.getMessage("text.paginador.titulo", null, locale));
		model.addAttribute("clientes", clientes);
		model.addAttribute("page", pageRender);
		
		return "listar";
	}

	//También se pueden asegurar las rutas con  @Secured("Role_Permitido")
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/form")
	public String crear(Map<String, 
			Object> model, 
			Locale locale) {

		Cliente cliente = new Cliente();

		model.put("cliente", cliente);
		model.put("titulo", messageSource.getMessage("text.form.crear.titulo", null, locale));
		model.put("boton", messageSource.getMessage("text.form.crear.boton", null, locale));

		return "form";
	}

	@PreAuthorize("hasRole('ROLE_USER')")
	@RequestMapping(value = "/form/{id}")
	public String editar(@PathVariable(value = "id") Long id, 
			Map<String, Object> model, 
			RedirectAttributes flash, 
			Locale locale) {

		Cliente cliente = null;

		if (id > 0) {

			cliente = clienteService.findOne(id);

			if (cliente == null) {

				flash.addFlashAttribute("error", messageSource.getMessage("text.form.actualizar.noExiste", null, locale));

				return "redirect:/listar";
			}

		} else {

			flash.addFlashAttribute("error", messageSource.getMessage("text.form.actualizar.invalido", null, locale));

			return "redirect:/listar";
		}

		model.put("cliente", cliente);
		model.put("titulo", messageSource.getMessage("text.form.actualizar.titulo", null, locale));
		model.put("boton", messageSource.getMessage("text.form.actualizar.boton", null, locale));

		return "form";
	}

	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/eliminar/{id}")
	public String eliminar(@PathVariable(value = "id") Long id, 
			RedirectAttributes flash, 
			Locale locale) {

		if (id > 0) {

			Cliente cliente = clienteService.findOne(id);

			clienteService.delete(id);
			flash.addFlashAttribute("success", messageSource.getMessage("text.cliente.eliminar.eliminado", null, locale));

			if (uploadFileService.delete(cliente.getFoto())) {
				flash.addFlashAttribute("info", messageSource.getMessage("text.cliente.eliminar.foto", null, locale) + cliente.getFoto() + messageSource.getMessage("text.cliente.eliminar.eliminada", null, locale));
			}

		}

		return "redirect:/listar";
	}

	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/form", method = RequestMethod.POST)
	// Pasamos el @ModelAttribute("nombreObjeto") cuando el objeto "cliente" no se
	// llame igual que la clase, Ejemplo: objeto cliente1 y clase Cliente
	public String guardar(@Valid Cliente cliente, 
			BindingResult result, 
			Model model,
			@RequestParam("file") MultipartFile foto, 
			RedirectAttributes flash, 
			SessionStatus status, 
			Locale locale) {

		if (cliente.getId() == null) {
			model.addAttribute("boton", messageSource.getMessage("text.form.crear.boton", null, locale));
		} else {
			model.addAttribute("boton", messageSource.getMessage("text.form.actualizar.boton", null, locale));
		}

		if (result.hasErrors()) {
			model.addAttribute("titulo", messageSource.getMessage("text.form.crear.titulo", null, locale));
			return "form";
		}

		// Obteniendo, manejando y guardando la imagen
		if (!foto.isEmpty()) {

			/*
			 * Guardar en carpeta del proyecto Path directorioRecursos =
			 * Paths.get("src/main/resources/static/uploads"); String rootPath =
			 * directorioRecursos.toFile().getAbsolutePath();
			 */

			// Borramos la foto anterior
			if (cliente.getId() != null && cliente.getId() > 0 && cliente.getFoto() != null
					&& cliente.getFoto().length() > 0) {

				uploadFileService.delete(cliente.getFoto());

			}

			String uniqueFileName = null;

			try {
				uniqueFileName = uploadFileService.copy(foto);
			} catch (IOException e) {
				e.printStackTrace();
			}

			flash.addFlashAttribute("info", messageSource.getMessage("text.form.guardar.mensaje", null, locale) + uniqueFileName + "'");
			cliente.setFoto(uniqueFileName);

		}

		String mensajeFlash = (cliente.getId() != null) ? messageSource.getMessage("text.form.guardar.editado", null, locale) : messageSource.getMessage("text.form.guardar.creado", null, locale);
		flash.addFlashAttribute("success", mensajeFlash);

		clienteService.save(cliente);
		status.setComplete();

		return "redirect:/listar";
	}

	@Secured({"ROLE_USER"})
	@GetMapping("/uploads/{filename:.+}")
	public ResponseEntity<Resource> verFoto(@PathVariable String filename) {

		Resource recurso = null;

		try {
			recurso = uploadFileService.load(filename);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + recurso.getFilename() + "\"")
				.body(recurso);

	}

	@PreAuthorize("hasRole('ROLE_USER')") //También podemos usar PreAuthorize
	@GetMapping("/ver/{id}")
	public String ver(@PathVariable(value = "id") Long id, 
			Map<String, Object> model, 
			RedirectAttributes flash, 
			Locale locale) {

		Cliente cliente = clienteService.fetchByIdWithFactura(id);

		if (cliente == null) {

			flash.addFlashAttribute("error", messageSource.getMessage("text.detalles.clienteNoExiste", null, locale));

			return "redirect:/listar";
		}

		model.put("cliente", cliente);
		model.put("titulo", messageSource.getMessage("text.detalles.titulo", null, locale) + cliente.getNombre() + " " + cliente.getApellido());

		return "ver";
	}

	// Obteniendo los roles
	private boolean hasRole(String role) {

		SecurityContext context = SecurityContextHolder.getContext();

		if (context == null) {
			return false;
		}

		Authentication auth = context.getAuthentication();

		if (auth == null) {
			return false;
		}

		Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();

		// Primera forma
		return authorities.contains(new SimpleGrantedAuthority(role));

		// Segunda forma
		/*
		 * for(GrantedAuthority authority : authorities) {
		 * 
		 * if(role.equals(authority.getAuthority())) {
		 * logger.info("Hola usuario".concat(auth.getName()).concat(". Tu rol es: "
		 * .concat(authority.getAuthority()))); return true; }
		 * 
		 * }
		 * 
		 * return false;
		 */
	}

}
