package com.shanzhu.book.web;

import com.shanzhu.book.mapper.BookWishMapper;
import com.shanzhu.book.model.BookWish;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/wish")
public class BookWishController {

    @Resource
    private BookWishMapper bookWishMapper;

    @PostMapping("/add")
    public Integer addWish(@RequestBody BookWish wish) {
        return bookWishMapper.insertWish(wish);
    }

    @GetMapping("/list")
    public List<BookWish> getWishList() {
        return bookWishMapper.selectAllWishes();
    }

    @PostMapping("/fulfill")
    public Integer fulfillWish(Integer wishId, Integer fulfillUserId) {
        return bookWishMapper.fulfillWish(wishId, fulfillUserId);
    }

    @PostMapping("/delete")
    public Integer deleteWish(Integer wishId) {
        return bookWishMapper.deleteWish(wishId);
    }
}