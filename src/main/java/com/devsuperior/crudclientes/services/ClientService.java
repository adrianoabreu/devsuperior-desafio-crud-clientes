package com.devsuperior.crudclientes.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import com.devsuperior.crudclientes.dto.ClientDTO;
import com.devsuperior.crudclientes.entities.Client;
import com.devsuperior.crudclientes.repositories.ClientRepository;
import com.devsuperior.crudclientes.services.exceptions.DatabaseException;
import com.devsuperior.crudclientes.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ClientService {

	@Autowired
	private ClientRepository repository;
	
	@Transactional(readOnly =  true)
	public ClientDTO findById(Long id) {
//      Este trecho de codigo é somente uma maneira mais didatica de retornar DTO
//		Optional<Product> result = repository.findById(id);
//		Product product = result.get();
//		ProductDTO dto = new ProductDTO(product);
//		return dto;
		
//		Este trecho de codigo é a maneira mais avançada em retornar um DTO
		Client product = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Recurso não encontrado"));
		return new ClientDTO(product);		
	}
	
	@Transactional(readOnly =  true)
//	public List<ProductDTO> findAll() {
	public Page<ClientDTO> findAll(Pageable pageable) {
//		List<Product> result = repository.findAll();
		Page<Client> result = repository.findAll(pageable);
		return result.map(x -> new ClientDTO(x));
	}
	
	@Transactional
	public ClientDTO insert(ClientDTO dto) {
		Client entity = new Client();
		copyDtoToEntity(dto, entity);
		entity = repository.save(entity);
		
		return new ClientDTO(entity);		
	}
	
	@Transactional
	public ClientDTO update(Long id, ClientDTO dto) {
		try {
			Client entity = repository.getReferenceById(id);
			copyDtoToEntity(dto, entity);	
			entity = repository.save(entity);		
			return new ClientDTO(entity);
		}
		catch(EntityNotFoundException e){
			throw new ResourceNotFoundException("Recurso não encontrado");
		}
				
	}
	
	@Transactional(propagation = Propagation.SUPPORTS)
	public void delete(Long id) {
		if (!repository.existsById(id)) {
			throw new ResourceNotFoundException("Recurso não encontrado");
		}
		try {
	        	repository.deleteById(id);    		
		}
	    	catch (DataIntegrityViolationException e) {
	        	throw new DatabaseException("Falha de integridade referencial");
	   	}
	}

	private void copyDtoToEntity(ClientDTO dto, Client entity) {
		entity.setName(dto.getName());
		entity.setIncome(dto.getIncome());
		entity.setBirthDate(dto.getBirthDate());
		entity.setChildren(dto.getChildren());
	}
}
