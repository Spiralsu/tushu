import request from '@/utils/request'
import { queryBookTypes } from '@/api/booktype'
import { queryBookInfosByPage, queryBookInfos } from '@/api/bookinfo'
import { queryBorrowsByPage } from '@/api/borrow'

// 获取图书类型分布数据
export async function getBookTypeDistribution() {
  try {
    const types = await queryBookTypes()

    const result = await Promise.all(types.map(async (type) => {
      const params = {
        page: 1,
        limit: 1,
        booktypeid: type.booktypeid
      }
      const response = await queryBookInfosByPage(params)
      return {
        name: type.booktypename,
        value: response.count || 0
      }
    }))

    return result
  } catch (error) {
    console.error('获取图书类型分布数据失败:', error)
    return []
  }
}

// 【核心修复】获取借阅状态分布数据
export async function getBorrowStatusDistribution() {
  try {
    const books = await queryBookInfos()

    let borrowedCount = 0 // 漂流中
    let availableCount = 0 // 在馆

    books.forEach(book => {
      // 容错处理：确保是数字
      const total = book.bookcount || 1;
      const current = book.inventory !== undefined ? book.inventory : 1;

      // 在馆 = 当前库存
      availableCount += current;
      // 漂流中 = 总数 - 库存
      const borrowed = total - current;
      if(borrowed > 0) {
        borrowedCount += borrowed;
      }
    })

    return {
      categories: ['在馆图书', '漂流中图书'],
      values: [availableCount, borrowedCount]
    }
  } catch (error) {
    console.error('获取借阅状态分布数据失败:', error)
    return {
      categories: [],
      values: []
    }
  }
}

// 获取高价图书排行榜
export async function getHighPriceBooks() {
  try {
    const books = await queryBookInfos()

    const pricedBooks = books
      .filter(book => book.bookname && book.bookprice)
      .map(book => ({
        name: book.bookname,
        value: parseFloat(book.bookprice) || 0
      }))
      .sort((a, b) => b.value - a.value)
      .slice(0, 6)

    return {
      books: pricedBooks.map(book => book.name),
      values: pricedBooks.map(book => book.value)
    }
  } catch (error) {
    console.error('获取图书价格统计数据失败:', error)
    return {
      books: [],
      values: []
    }
  }
}
