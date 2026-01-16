alter table wish_list
drop foreign key fk_wishlist_on_product;

alter table wish_list
    add constraint fk_wishlist_on_product
        foreign key (product_id) references products (id)
            on delete cascade;

