import request from '@/utils/request'

export function uploadFile(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/file/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export function uploadFiles(files) {
  const formData = new FormData()
  files.forEach(file => {
    formData.append('files', file)
  })
  return request.post('/file/upload/batch', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export function addImageByUrl(url) {
  return request.post('/file/add-url', null, {
    params: { url }
  })
}