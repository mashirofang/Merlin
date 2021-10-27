package com.merlin.api;

/**
 * 类型转换器 , 原始类型和目标类型间的互相转换
 *
 * @param <SOURCE> 原始类型
 * @param <TARGET> 目标类型
 */
public interface Converter<SOURCE, TARGET> {

  /**
   * 将原始类型的实例转换为目标类型
   *
   * @param source 原始类型
   * @return
   */
  TARGET sourceToTarget(SOURCE source);

  /**
   * 将目标类型的实例转回原始类型
   *
   * @param target 目标类型
   * @return
   */
  SOURCE targetToSource(TARGET target);

  /**
   * 获取原始类型的 class
   *
   * @return
   */
  Class<SOURCE> getSourceType();

  /**
   * 获取目标类型的class
   *
   * @return
   */
  Class<TARGET> getTargetType();
}
