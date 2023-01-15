import TimeAgo from "javascript-time-ago";

import zh from "javascript-time-ago/locale/zh"

TimeAgo.addDefaultLocale(zh)

const timeAgo = new TimeAgo('zh-Hans')

export function formatTimeAgo(date) {
  date = date instanceof Date ? date : new Date(date)
  return timeAgo.format(date)
}

const PATH_PATTERN = /(\/[-a-z\d%_.~+]*)+/

export function checkPath(path) {
  return PATH_PATTERN.test(path) && path.startsWith("/api")
}

export function isEmpty(str) {
  return (!str || str.length === 0);
}

export function getOrElse(str, other) {
  return isEmpty(str) ? other : str
}

export function titleString(string) {
  return string[0].toUpperCase() + string.slice(1);
}