package com.jasim.store.mappers;

import com.jasim.store.dtos.ProductDto;
import com.jasim.store.entities.Product;
import lombok.Getter;
import lombok.Setter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(source = "category.id", target = "categoryId")
    ProductDto toDto(Product product);


    Product toEntity(ProductDto productDto);

    @Mapping(target = "id", ignore = true)
    void toEntityUpdate(ProductDto productDto, @MappingTarget Product product);
}
