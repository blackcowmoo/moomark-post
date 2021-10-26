package com.moomark.board.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moomark.board.domain.Board;
import com.moomark.board.domain.BoardCategory;
import com.moomark.board.domain.BoardDto;
import com.moomark.board.domain.Category;
import com.moomark.board.exception.ErrorCode;
import com.moomark.board.exception.JpaException;
import com.moomark.board.repository.BoardCategoryRepository;
import com.moomark.board.repository.BoardRepository;
import com.moomark.board.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

  private final BoardRepository boardRepository;
  private final CategoryRepository categoryRepository;
  private final BoardCategoryRepository boardCategoryRepository;

  public Long saveBoard(BoardDto boardDto) {
    log.info("add Board : {}", boardDto);

    var board = Board.builder().title(boardDto.getTitle()).authorId(boardDto.getAuthorId())
        .content(boardDto.getContent()).build();

    return boardRepository.save(board).getId();
  }

  /**
   * delete board by board id
   * 
   * @param boardId
   * @throws JpaException
   */
  public void deleteBoard(Long boardId) throws JpaException {
    var board = boardRepository.findById(boardId)
        .orElseThrow(() -> new JpaException(ErrorCode.CANNOT_FIND_BOARD.getMsg(),
            ErrorCode.CANNOT_FIND_BOARD.getCode()));
    boardRepository.delete(board);
  }

  /**
   * Get board information by board id
   * 
   * @param id
   * @return
   * @throws JpaException
   */
  public BoardDto getBoardInfoById(Long id) throws JpaException {
    var board = boardRepository.findById(id)
        .orElseThrow(() -> new JpaException(ErrorCode.CANNOT_FIND_BOARD.getMsg(),
            ErrorCode.CANNOT_FIND_BOARD.getCode()));
    board.upCountViewCount();
    return BoardDto.builder().id(board.getId()).authorId(board.getAuthorId())
        .content(board.getContent()).title(board.getTitle()).uploadTime(board.getUploadTime())
        .recommendCount(board.getRecommendCount()).viewsCount(board.getViewsCount()).build();
  }

  /**
   * Get board Information by Title
   * 
   * @param title
   * @return
   */
  public List<BoardDto> getBoardInfoByTitle(String title) {
    var boardList = boardRepository.findByTitle(title);
    List<BoardDto> boardDtoList = new ArrayList<>();
    for (Board board : boardList) {
      var boardDto =
          BoardDto.builder().id(board.getId()).title(board.getTitle()).authorId(board.getAuthorId())
              .content(board.getContent()).uploadTime(board.getUploadTime())
              .viewsCount(board.getViewsCount()).recommendCount(board.getRecommendCount()).build();
      boardDtoList.add(boardDto);

    }

    return boardDtoList;
  }

  /**
   * Add category to board
   * 
   * @param boardId
   * @param categoryId
   * @throws JpaException
   */
  public void addCategoryToBoard(Long boardId, Long categoryId) throws JpaException {
    var board = boardRepository.findById(boardId)
        .orElseThrow(() -> new JpaException(ErrorCode.CANNOT_FIND_BOARD.getMsg()));
    var category = categoryRepository.findById(categoryId).orElseThrow();

    boardCategoryRepository.save(BoardCategory.builder().board(board).category(category).build());
  }

  /**
   * delete category to board
   * 
   * @param boardId
   * @param categoryId
   * @throws JpaException
   */
  public void deleteCategoryToBoard(Long boardId, Long categoryId) throws JpaException {
    var board = boardRepository.findById(boardId)
        .orElseThrow(() -> new JpaException(ErrorCode.CANNOT_FIND_BOARD.getMsg(),
            ErrorCode.CANNOT_FIND_BOARD.getCode()));
    var category = categoryRepository.findById(categoryId)
        .orElseThrow(() -> new JpaException(ErrorCode.CANNOT_FIND_CATEGORY.getMsg(),
            ErrorCode.CANNOT_FIND_CATEGORY.getCode()));

    var boardCategory = boardCategoryRepository.findByBoardAndCategory(board, category);
    boardCategoryRepository.delete(boardCategory);
  }

  /**
   * Get board list by category id
   * 
   * @param categoryId
   * @return
   * @throws Exception
   */
  public List<BoardDto> getBoardListByCategory(Long categoryId) throws JpaException {
    Category category = categoryRepository.findById(categoryId)
        .orElseThrow(() -> new JpaException(ErrorCode.CANNOT_FIND_CATEGORY.getMsg(),
            ErrorCode.CANNOT_FIND_CATEGORY.getCode()));
    List<BoardCategory> getBoardList = boardCategoryRepository.findByCategory(category);
    List<BoardDto> resultList = new ArrayList<>();
    for (BoardCategory board : getBoardList) {
      resultList.add(BoardDto.builder().id(board.getBoard().getId())
          .title(board.getBoard().getTitle()).authorId(board.getBoard().getAuthorId())
          .recommendCount(board.getBoard().getRecommendCount())
          .viewsCount(board.getBoard().getViewsCount()).build());
    }

    return resultList;
  }
}
