package com.github.jpaquerydslmybatis.web.controller.music;


import com.github.jpaquerydslmybatis.service.music.MusicTestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/music")
public class MusicTestController {
    private final MusicTestService musicTestService;
    @GetMapping("/list")
    public Object musicList(@RequestParam String keyword) {
        Object musicList = musicTestService.getMusicList(keyword);
        return musicList;
    }
    @GetMapping("/list2")
    public Object musicList2(@RequestParam String keyword) {
        Object musicList = musicTestService.searchTracksRequest(keyword);
        return musicList;
    }
    @GetMapping
    public Object musicList3(@RequestParam String musicId) {
        Object music = musicTestService.getMusicById(musicId);
        return music;

    }
}
