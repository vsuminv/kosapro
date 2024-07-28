//package exam.boardExam.service;
//
//import exam.boardExam.domain.Article;
//import exam.boardExam.dto.AddArticleRequest;
//import exam.boardExam.dto.UpdatedArticleRequest;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//
//@RequiredArgsConstructor
//@Service
//public class BlogService {
//    private final BlogRepository blogRepository;
//
//    public Article save(AddArticleRequest request){
//        return blogRepository.save(request.toEntity());
//    }
//
//    public List<Article> findAll(){
//        return blogRepository.findAll();
//    }
//
//    public Article findById(long id){
//        return blogRepository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("not found : " + id));
//    }
//
//    public void delete(long id){
//        blogRepository.deleteById(id);
//    }
//
//    @Transactional
//    public Article update(long id, UpdatedArticleRequest request){
//        Article article = blogRepository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("not found : " + id));
//        article.update(request.getTitle(), request.getContent());
//
//        return article;
//    }
//}
