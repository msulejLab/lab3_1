/*
 * Copyright 2011-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pl.com.bottega.ecommerce.sales.application.api.handler;

import pl.com.bottega.cqrs.command.handler.CommandHandler;
import pl.com.bottega.ecommerce.sales.application.api.command.AddProductCommand;
import pl.com.bottega.ecommerce.sales.domain.client.Client;
import pl.com.bottega.ecommerce.sales.domain.client.ClientRepository;
import pl.com.bottega.ecommerce.sales.domain.equivalent.SuggestionService;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.Product;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductRepository;
import pl.com.bottega.ecommerce.sales.domain.reservation.Reservation;
import pl.com.bottega.ecommerce.sales.domain.reservation.ReservationRepository;
import pl.com.bottega.ecommerce.system.application.SystemContext;



public class AddProductCommandHandler implements CommandHandler<AddProductCommand, Void>{


	private ReservationRepository reservationRepository;
	

	private ProductRepository productRepository;
	

	private SuggestionService suggestionService;
	

	private ClientRepository clientRepository;
	

	private SystemContext systemContext;
	
	@Override
	public Void handle(AddProductCommand command) {
		Reservation reservation = reservationRepository.load(command.getOrderId());
		
		Product product = productRepository.load(command.getProductId());
		
		if (! product.isAvailable()){
			Client client = loadClient();	
			product = suggestionService.suggestEquivalent(product, client);
		}
			
		reservation.add(product, command.getQuantity());
		
		reservationRepository.save(reservation);
		
		return null;
	}
	
	private Client loadClient() {
		return clientRepository.load(systemContext.getSystemUser().getClientId());
	}

	public void setReservationRepository(ReservationRepository reservationRepository) {
		this.reservationRepository = reservationRepository;
	}

	public void setProductRepository(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	public void setSuggestionService(SuggestionService suggestionService) {
		this.suggestionService = suggestionService;
	}

	public void setClientRepository(ClientRepository clientRepository) {
		this.clientRepository = clientRepository;
	}

	public void setSystemContext(SystemContext systemContext) {
		this.systemContext = systemContext;
	}
}
