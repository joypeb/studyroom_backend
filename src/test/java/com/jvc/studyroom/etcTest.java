package com.jvc.studyroom;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class etcTest {


  @Nested
  @DisplayName("Strnig 함수 객체에 null이 들어 갔을 때 에러를 일으킨다.")
  class nullTest {

    @Test
    void test() {
      String nullStr = null;
      Assertions.assertThrows(NullPointerException.class, () -> {
        nullStr.startsWith("h");
      });
    }
  }

  @Nested
  @DisplayName(".flatmap 기능 확인하기")
  class flatMapTest {

    @Test
    void doFlatMap() {
      // 학생들의 과목별 점수
      class Student {

        String name;
        List<Integer> scores;

        public Student(String name, List<Integer> scores) {
          this.name = name;
          this.scores = scores;
        }
      }
      List<Student> students = Arrays.asList(
          new Student("김철수", Arrays.asList(90, 85, 88)),
          new Student("이영희", Arrays.asList(92, 87))
      );
      // map: 각 학생의 점수 리스트 (중첩)
      List<List<Integer>> allScoreLists = students.stream()
          .map(student -> student.scores)
          .collect(Collectors.toList());
      // [[90, 85, 88], [92, 87]]
      System.out.println(allScoreLists);

      // flatMap: 모든 점수를 하나의 리스트로
      List<Integer> allScores = students.stream()
          .peek(
              student -> System.out.println("평탄화 전 개별요소 : " + student.name + "점수" + student.scores))
          .flatMap(student -> {
            return student.scores.stream();
          })
          .peek(studentScore -> System.out.println("평탄화 완료된 개별요소 :" + studentScore))
          .collect(Collectors.toList());
      // [90, 85, 88, 92, 87]
      System.out.println(allScores);
    }
  }

}
