package com.casino.grupo4_dws.casinoweb.mapper;

import com.casino.grupo4_dws.casinoweb.dto.PrizeDTO;
import com.casino.grupo4_dws.casinoweb.model.Prize;
import com.casino.grupo4_dws.casinoweb.model.User;
import com.casino.grupo4_dws.casinoweb.repos.UserRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring")
public abstract class PrizeMapper {
    @Autowired
    protected UserRepository userRepository;

    @Mapping(target = "owner", source = "owner", qualifiedByName = "userToId")
    public abstract PrizeDTO toDTO(Prize prize);

    @Mapping(target = "owner", source = "owner", qualifiedByName = "idToUser")
    public abstract Prize toEntity(PrizeDTO prizeDTO);

    public abstract List<PrizeDTO> toDTOList(List<Prize> prizes);

    public abstract List<Prize> toEntityList(List<PrizeDTO> prizeDTOs);

    @Named("userToId")
    public int userToId(User user) {
        if (user == null) return 0;
        return user.getId();
    }

    @Named("idToUser")
    public User idToUser(int id) {
        if (id == 0) return null;
        Optional<User> user = userRepository.findById((long)id);
        if (!user.isPresent()) return null;
        return user.get();
    }
}