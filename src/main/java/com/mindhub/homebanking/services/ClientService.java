package com.mindhub.homebanking.services;

import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.models.Client;


import java.util.List;

public interface ClientService {


    List<Client> findAllClients();

    List<ClientDTO> mapClientsDTO(List<Client> clients);

    Client findById(Long id);

    ClientDTO getClientsDTOById(Long id);

    Client findByEmail(String email);

    void saveClient(Client client);

    ClientDTO getClientCurrent(String email);

}
